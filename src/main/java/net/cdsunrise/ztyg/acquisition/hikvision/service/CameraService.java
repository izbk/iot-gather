package net.cdsunrise.ztyg.acquisition.hikvision.service;

import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.Camera;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.vo.CameraVo;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-19 13:59
 */
public interface CameraService {

    /**
     *
     * @return 摄像机基础数据
     */
    List<Camera> getAll();

    /**
     *
     * @param deviceCode 设备编码
     * @return 相机基础信息
     */
    CameraVo getCameraByDeviceCode(String deviceCode);
}
