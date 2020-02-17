package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sh
 * @date 2019-12-11 11:16
 */
@Data
public class ExtEventIdentityCardInfo {
    /** 住址，长度512 */
    @JsonProperty("Address")
    private String address;
    /** 出生日期，长度32 */
    @JsonProperty("Birth")
    private String birth;
    /** 有效日期结束时间，长度32 */
    @JsonProperty("EndDate")
    private String endDate;
    /** 身份证id，长度32 */
    @JsonProperty("IdNum")
    private String idNum;
    /** 签发机关，长度32 */
    @JsonProperty("IssuingAuthority")
    private String issuingAuthority;
    /** 姓名，长度32 */
    @JsonProperty("Name")
    private String name;
    /** 民族，长度32 */
    @JsonProperty("Nation")
    private String nation;
    /** 性别 */
    @JsonProperty("Sex")
    private String sex;
    /** 有效日期开始时间，长度32 */
    @JsonProperty("StartDate")
    private String startDate;
    /**
     * 是否长期有效
     * 0-否（有效截止日期有效）
     * 1-是（有效截止日期无效）
     * */
    @JsonProperty("TermOfValidity")
    private Integer termOfValidity;
}
