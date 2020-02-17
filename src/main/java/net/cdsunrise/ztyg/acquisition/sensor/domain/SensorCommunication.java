package net.cdsunrise.ztyg.acquisition.sensor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 传感器通信数据
 * @author Binke Zhang
 * @date 2019-11-25 11:27
 */
@Data
@TableName("t_sensor_communication")
public class SensorCommunication {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** slave id */
    private Integer slaveId;
    /** IP地址 */
    private String ip;
    /** 端口*/
    private Integer port;
    /** 寄存器读地址 */
    private Integer readRegister;
    /** 传感器类型 */
    private Integer type;
    /** 上线时间 */
    private Date uptime;
}
