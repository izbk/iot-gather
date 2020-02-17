package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import lombok.Data;

/**
 * @author sh
 * @date 2019-12-13 09:50
 */
@Data
public class CardInfo {
    /** 卡片ID */
    private String cardId;
    /** 卡号 */
    private String cardNo;
    /** 持卡人员id */
    private String personId;
    /** 持卡人名称 */
    private String personName;
    /** 使用状态标记 */
    private Integer useStatus;
    /** 生效日期，遵守ISO8601标准 */
    private String startDate;
    /** 失效日期，遵守ISO8601标准 */
    private String endDate;
    /** 挂失时间，遵守ISO8601标准 */
    private String lossDate;
    /** 解除挂失时间，遵守ISO8601标准 */
    private String unlossDate;
}
