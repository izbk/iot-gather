package net.cdsunrise.ztyg.acquisition.sensor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * CO2传感器通数据
 * @author Binke Zhang
 * @date 2019/12/3 10:56
 */
@Data
@TableName("t_sensor_co2")
public class SensorCO2 {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * CO2浓度
     */
    private Float co2Concentration;

    /**
     * 创建时间
     */
    private Date createTime;
}
