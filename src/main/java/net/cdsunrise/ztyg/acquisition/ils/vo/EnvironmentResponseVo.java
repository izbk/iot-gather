package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;

import java.util.Date;

/**
 * 环境参数上报结果
 * @author Binke Zhang
 * @date 2019/11/29 11:53
 */
@Data
public class EnvironmentResponseVo {
    private String deviceCode;
    private Short floor;
    private String room;
    private Integer temperature;
    private Integer humidity;
    private Integer pm25;
    private Date updateTime;
    private Date uptime;
}
