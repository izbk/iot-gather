package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * @author Binke Zhang
 * @date 2020/2/18 15:40
 */
@Data
public class CardInfoVo {
    private String cardNo;
    private String cardName;
    /**
     * 记录集编号
     */
    private int recordNo;
    private String userId;
    /**
     * 卡密码
     */
    private String password;
    private String status;
    private String type;
    private String useTime;
    /**
     * 是否首卡
     */
    private String firstEnter;
    /**
     * 是否有效
     */
    private String valid;
    private String startTime;
    private String endTime;
}
