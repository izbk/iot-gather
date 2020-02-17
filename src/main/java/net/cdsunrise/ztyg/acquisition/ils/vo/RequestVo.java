package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;

/**
 * 请求实体
 * @author Binke Zhang
 * @date 2019/11/29 11:53
 */
@Data
public class RequestVo {
    private String deviceCode;
    private Integer command;
}
