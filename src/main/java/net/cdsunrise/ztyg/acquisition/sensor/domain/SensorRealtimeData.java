package net.cdsunrise.ztyg.acquisition.sensor.domain;

import lombok.Data;

import java.util.Date;

/**
 * 传感器通用实时数据
 * @author Binke Zhang
 * @date 2019/12/3 10:56
 */
@Data
public class SensorRealtimeData {

    /**
     * 传感器类型
     */
    private Integer sensorType;
    /**
     * 运行状态
     */
    private Integer runningState;
    /**
     * 通信状态
     */
    private Integer communicateState;
    /**
     * 运行时间(h)
     */
    private Long runningTime;
    /**
     * 故障次数
     */
    private Integer faultTime;
    /**
     * 技术状态
     */
    private String technicalState;
    /**
     * 评价等级
     */
    private String evaluationGrade;
    /**
     * 上线时间
     */
    private Date uptime;
    /**
     * CO2浓度
     */
    private String co2Concentration;
    /**
     * 温度
     */
    private String temperature;
    /**
     * 湿度
     */
    private String humidity;
    /**
     * 数据更新时间
     */
    private Date updateTime;
}
