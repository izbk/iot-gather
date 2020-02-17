package net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.vo;

import lombok.Data;

/**
 * @author sh
 * @date 2019-12-23 11:31
 */
@Data
public class CameraVo {
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** 监控点唯一标识 */
    private String cameraIndexCode;
    /** 监控点名称 */
    private String cameraName;
    /** 监控点类型说明 */
    private String cameraTypeName;
    /** 通道类型说明 */
    private String channelTypeName;
    /** 监控点视频地址 */
    private String cameraUrl;
}
