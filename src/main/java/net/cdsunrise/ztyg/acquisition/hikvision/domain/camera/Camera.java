package net.cdsunrise.ztyg.acquisition.hikvision.domain.camera;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author sh
 * @date 2019-12-19 13:55
 */
@Data
@TableName("t_camera")
public class Camera {
    @TableId(type = IdType.AUTO)
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
}
