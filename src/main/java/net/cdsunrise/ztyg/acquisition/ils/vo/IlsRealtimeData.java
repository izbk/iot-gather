package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;

import java.util.Date;

/**
 * 环境参数上报结果
 * @author Binke Zhang
 * @date 2019/11/29 11:53
 */
@Data
public class IlsRealtimeData {
    private String deviceCode;
    private Short floor;
    private String room;
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
    private Integer temperature;
    private Integer humidity;
    private Integer pm25;
    private Date updateTime;

}
