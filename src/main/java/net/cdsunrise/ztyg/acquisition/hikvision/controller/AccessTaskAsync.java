package net.cdsunrise.ztyg.acquisition.hikvision.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.HikDataResult;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.*;
import net.cdsunrise.ztyg.acquisition.hikvision.service.AccessService;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sh
 * @date 2019-12-12 14:58
 */
@Slf4j
@Component
public class AccessTaskAsync {

    private static final DateTimeFormatter DTF_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final AccessService accessService;
    @Value("${spring.resources.static-locations}")
    private String[] pictureAddr;

    @Autowired
    public AccessTaskAsync(AccessService accessService) {
        this.accessService = accessService;
    }

    @Async
    public void execute(AccessMesResp resp) {
        long start = System.currentTimeMillis();
        // 校验：如果卡号、图片的url、图片服务器唯一编码为空则不处理
        List<Event> events = resp.getParams().getEvents();
        if (events != null && events.size() > 0) {
            Event event = events.get(0);
            EventData data = event.getData();
            // 获取卡号，如果卡号为空则不继续处理
            if (data.getExtEventCardNo() == null) {
                return;
            }
            // 如果图片url为空则不处理
            if (StringUtil.isEmpty(data.getExtEventPictureURL())) {
                return;
            }
            // 如果图片服务器唯一编码为空则不处理
            if (StringUtil.isEmpty(data.getSvrIndexCode())) {
                return;
            }
        } else {
            log.info("[AccessTaskAsync] access event is null");
            return;
        }
        // 开始处理业务逻辑
        doExecute(resp);
        long end = System.currentTimeMillis();
        log.info("[AccessTaskAsync] 异步处理闸机人员信息耗时：[{}] ms", (end - start));
    }

    private void doExecute(AccessMesResp resp) {
        // 根据卡号获取人员信息
        Event event = resp.getParams().getEvents().get(0);
        EventData data = event.getData();
        Integer cardNo = data.getExtEventCardNo();
        PersonInfo personInfo = getPerson(cardNo);
        // 根据图片相对url获取图片绝对地址
        String photoUrl = getPictureUrl(AccessController.getPath(AccessController.ARTEMIS_PATH + "/api/resource/v1/person/picture"),
                personInfo.getPersonPhoto().getPicUri(), personInfo.getPersonPhoto().getServerIndexCode(), "serverIndexCode", pictureAddr[0]);
        // 根据图片url和图片服务器唯一编码获取抓怕图片地址
        String eventPictureUrl = getPictureUrl(AccessController.getPath(AccessController.ARTEMIS_PATH + "/api/acs/v1/event/pictures"),
                data.getExtEventPictureURL(), data.getSvrIndexCode(), "svrIndexCode", pictureAddr[1]);
        PersonnelAccessHistoryDTO history = new PersonnelAccessHistoryDTO();
        history.setName(personInfo.getPersonName());
        history.setJobNo(personInfo.getJobNo());
        history.setCarNo(cardNo);
        history.setPersonId(personInfo.getPersonId());
        history.setOrgPathName(personInfo.getOrgName());
        history.setSrcName(event.getSrcName());
        history.setAccess("入");
        history.setEventType(event.getEventType());
        history.setEventTypeName("人脸认证通过");
        history.setExtEventPictureUrl(eventPictureUrl);
        history.setPhotoUrl(photoUrl);
        String happenTime = event.getHappenTime();
        if (!StringUtil.isEmpty(happenTime)) {
            history.setHappenTime(LocalDateTime.parse(happenTime, DTF_ISO));
        }
        history.setCreateTime(LocalDateTime.now());
        PersonnelAccessRealDTO real = new PersonnelAccessRealDTO();
        BeanUtils.copyProperties(history, real);
        accessService.insertHistory(history);
        accessService.insertReal(real);
    }

    /**
     * 获取人员图片绝对地址
     * @param picUri 图片相对url
     * @param serverIndexCode 图片服务器唯一标识
     * @return 图片绝对地址
     */
    private String getPictureUrl(Map<String, String> path, String picUri, String serverIndexCode, String indexCodeKey, String filePath) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("picUri", picUri);
        jsonObject.put(indexCodeKey, serverIndexCode);
        String body = jsonObject.toJSONString();
        HttpResponse response = ArtemisHttpUtil.doPostStringImgArtemis(path, body, null, null, AccessController.CONTENT_TYPE, null);
        InputStream is = null;
        FileOutputStream os = null;
        if (response != null) {
            try {
                String filename = UUID.randomUUID().toString() + ".jpg";
                is = response.getEntity().getContent();
                os = new FileOutputStream(new File(filePath.replace("file:", "") + "\\" + filename));
                byte[] b = new byte[1024];
                while ((is.read(b) != -1)) {
                    os.write(b);
                }
                os.flush();
                return filename;
            } catch (IOException e) {
                log.error("", e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return null;
    }

    /**
     * 根据卡号获取人员信息
     * @param extEventCardNo 卡号
     * @return 人员信息
     */
    private PersonInfo getPerson(Integer extEventCardNo) {
        // 获取卡片信息
        Map<String, String> cardPath = AccessController.getPath(AccessController.ARTEMIS_PATH + "/api/irds/v1/card/cardInfo");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cardNo", extEventCardNo);
        String carResult = ArtemisHttpUtil.doPostStringArtemis(cardPath, jsonObject.toJSONString(), null,
                null, AccessController.CONTENT_TYPE, null);
        log.info("[AccessTaskAsync] get carInfo by carId, id: [{}] result[{}]", extEventCardNo, carResult);
        Asserts.assertNotNull(carResult, () -> new BusinessException("car_info_err", "car info is null"));
        HikDataResult<CardInfo> cardInfoAccessDataResult = JSONObject.parseObject(carResult,
                new TypeReference<HikDataResult<CardInfo>>() {
        }.getType());
        // 根据personId查询人员信息
        String personId = cardInfoAccessDataResult.getData().getPersonId();
        Asserts.assertStringNotNull(personId, () -> new BusinessException("person_id_null", "person id is null"));
        Map<String, String> personPath = AccessController.getPath(AccessController.ARTEMIS_PATH + "/api/resource/v1/person/personId/personInfo");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("personId", personId);
        String personResult = ArtemisHttpUtil.doPostStringArtemis(personPath, jsonObject1.toJSONString(), null,
                null, AccessController.CONTENT_TYPE, null);
        Asserts.assertNotNull(personResult, () -> new BusinessException("person_info_null", "person info is null"));
        HikDataResult<PersonInfo> personInfoAccessDataResult = JSONObject.parseObject(personResult,
                new TypeReference<HikDataResult<PersonInfo>>() {
        }.getType());
        return personInfoAccessDataResult.getData();
    }

}
