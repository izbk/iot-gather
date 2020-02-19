package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * 测温点信息
 * @author Binke Zhang
 * @date 2020/2/18 16:30
 */
@Data
public class ThermalPointInfoVo {
    private String meterType;
    private String temperUnit;
    private String temper;
}
