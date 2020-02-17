package net.cdsunrise.ztyg.acquisition.hikvision.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.ztyg.acquisition.hikvision.controller.AccessController;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.HikDataResult;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.Camera;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.CameraUrlResp;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.vo.CameraVo;
import net.cdsunrise.ztyg.acquisition.hikvision.mapper.CameraMapper;
import net.cdsunrise.ztyg.acquisition.hikvision.service.CameraService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author sh
 * @date 2019-12-19 13:59
 */
@Slf4j
@Service
public class CameraServiceImpl implements CameraService {

    private final CameraMapper cameraMapper;

    @Autowired
    public CameraServiceImpl(CameraMapper cameraMapper) {
        this.cameraMapper = cameraMapper;
    }

    @Override
    public List<Camera> getAll() {
        return cameraMapper.selectList(null);
    }

    @Override
    public CameraVo getCameraByDeviceCode(String deviceCode) {
        QueryWrapper<Camera> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Camera::getDeviceCode, deviceCode);
        Camera camera = cameraMapper.selectOne(queryWrapper);
        CameraVo vo = new CameraVo();
        BeanUtils.copyProperties(camera, vo);
        Map<String, String> path = AccessController.getPath(AccessController.ARTEMIS_PATH + "/api/video/v1/cameras/previewURLs");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cameraIndexCode", camera.getCameraIndexCode());
        jsonObject.put("streamType", 1);
        jsonObject.put("protocol", "hls");
        jsonObject.put("transmode", 1);
        jsonObject.put("expand", "streamform=rtp");
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonObject.toJSONString(), null, null, AccessController.CONTENT_TYPE, null);
        if (!StringUtil.isEmpty(result)) {
            HikDataResult<CameraUrlResp> hikDataResult = JSONObject.parseObject(result, new TypeReference<HikDataResult<CameraUrlResp>>() {
            }.getType());
            vo.setCameraUrl(hikDataResult.getData().getUrl());
        }
        return vo;
    }
}
