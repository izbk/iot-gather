package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * @author Binke Zhang
 * @date 2020/2/18 15:17
 */
@Data
public class GateEventInfoVo {
    private String time;
    private String openStatus;
    private String openMethod;
    private String cardName;
    private String cardNo;
    private String userId;
}
