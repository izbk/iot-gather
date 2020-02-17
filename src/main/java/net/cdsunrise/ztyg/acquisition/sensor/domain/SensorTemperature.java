package net.cdsunrise.ztyg.acquisition.sensor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 温度传感器数据
 * @author Binke Zhang
 * @date 2019/12/3 10:56
 */
@Data
@TableName("t_sensor_temperature")
public class SensorTemperature {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 温度
     */
    private Float temperature;

    /**
     * 创建时间
     */
    private Date createTime;
}
