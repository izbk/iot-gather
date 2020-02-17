package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sh
 * @date 2019-12-11 11:16
 */
@Data
public class ExtEventCustomerNumInfo {
    /** 通道号 */
    @JsonProperty("AccessChannel")
    private Integer accessChannel;
    /** 进人数 */
    @JsonProperty("EntryTimes")
    private Integer entryTimes;
    /** 出人数 */
    @JsonProperty("ExitTimes")
    private Integer exitTimes;
    /** 总通行人数 */
    @JsonProperty("TotalTimes")
    private Integer totalTimes;
}
