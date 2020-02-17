package net.cdsunrise.ztyg.acquisition.sensor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 温湿度传感器数据
 * @author Binke Zhang
 * @date 2019/12/3 10:56
 */
@Data
@TableName("t_sensor_humidity")
public class SensorHumidity {

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
     * 湿度
     */
    private Float humidity;

    /**
     * 创建时间
     */
    private Date createTime;
}
