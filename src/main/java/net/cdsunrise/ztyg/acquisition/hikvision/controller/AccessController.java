package net.cdsunrise.ztyg.acquisition.hikvision.controller;

import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.hikvision.config.HikConfig;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.AccessMesResp;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.HikSubscriptionResp;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.PersonnelAccessRealDTO;
import net.cdsunrise.ztyg.acquisition.hikvision.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sh
 * @date 2019-12-11 11:44
 */
@Slf4j
@RestController
@RequestMapping("/access")
public class AccessController {

    public static final String ARTEMIS_PATH = "/artemis";
    public static final String CONTENT_TYPE = "application/json";

    private final HikConfig hikConfig;
    private final AccessTaskAsync accessTaskAsync;
    private final AccessService accessService;

    @Value("${server.port}")
    private Integer port;

    @Autowired
    public AccessController(HikConfig hikConfig, AccessTaskAsync accessTaskAsync, AccessService accessService) {
        this.hikConfig = hikConfig;
        this.accessTaskAsync = accessTaskAsync;
        this.accessService = accessService;
    }

    /**
     * 开始订阅
     */
    @PostConstruct
    public void subscription() {
        if (hikConfig.getEnableSubscription()) {
            Map<String, String> path = getPath(ARTEMIS_PATH + "/api/eventService/v1/eventSubscriptionByEventTypes");
            // 订阅人脸认证通过事件
            Integer[] eventTypes = new Integer[]{196893};
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eventTypes", eventTypes);
            // 该地址和方法 getSubscription 保持一致
            jsonObject.put("eventDest", "http://" + hikConfig.getNativeIp() + ":" + port + "/access/subscription");
            jsonObject.put("subType", 2);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonObject.toJSONString(), null, null, CONTENT_TYPE , null);
            if (StringUtil.isEmpty(result)) {
                throw new BusinessException("subscription_err", "闸机订阅失败，请重新订阅，或检查海康威视平台接口是否可访问...");
            }
            log.info("[AccessController] 订阅返回结果：[{}]", result);
            HikSubscriptionResp hikSubscriptionResp = JSONObject.parseObject(result, HikSubscriptionResp.class);
            if ("0".equals(hikSubscriptionResp.getCode())) {
                log.info("[AccessController] 闸机事件订阅成功...");
            } else {
                throw new BusinessException("subscription_err", "闸机订阅失败，请重新订阅，或检查海康威视平台接口是否可访问...");
            }
        }
    }

    @PostMapping("/subscription")
    public Result<Boolean> getSubscription(@RequestBody AccessMesResp resp) {
        log.info("[AccessController] access data is [{}]", resp);
        accessTaskAsync.execute(resp);
        return ResultUtil.success();
    }

    @GetMapping("/real")
    public Result<List<PersonnelAccessRealDTO>> getAccessRealData() {
        return ResultUtil.success(accessService.getRealAll());
    }

    /**
     * 取消订阅
     */
    @PreDestroy
    public void unsubscribe() {
        if (hikConfig.getEnableSubscription()) {
            Map<String, String> path = getPath(ARTEMIS_PATH + "/api/eventService/v1/eventUnSubscriptionByEventTypes");
            // 订阅人脸认证通过事件
            Integer[] eventTypes = new Integer[]{196893};
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eventTypes", eventTypes);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonObject.toJSONString(), null, null, CONTENT_TYPE , null);
            if (StringUtil.isEmpty(result)) {
                throw new BusinessException("unsubscription_err", "闸机取消订阅失败，请重新订阅，或检查海康威视平台接口是否可访问...");
            }
            log.info("[AccessController] 取消订阅返回结果：[{}]", result);
            HikSubscriptionResp hikSubscriptionResp = JSONObject.parseObject(result, HikSubscriptionResp.class);
            if ("success".equals(hikSubscriptionResp.getMsg())) {
                log.info("[AccessController] 闸机事件取消订阅成功...");
            } else {
                throw new BusinessException("unsubscription_err", "闸机取消订阅失败，请重新订阅，或检查海康威视平台接口是否可访问...");
            }
        }
    }

    public static Map<String, String> getPath(String path) {
        return new HashMap<String, String>(2) {
            {
                put("https://", path);//根据现场环境部署确认是http还是https
            }
        };
    }
}
