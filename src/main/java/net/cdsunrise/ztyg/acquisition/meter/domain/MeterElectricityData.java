package net.cdsunrise.ztyg.acquisition.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author sh
 * @date 2019-11-27 16:11
 */
@Data
@TableName("t_meter_electricity_data")
public class MeterElectricityData {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** 原始电量 */
    private Integer originalElectricity;
    /** 用电量，单位KWh */
    private Integer electricity;
    /** 采集时间 */
    private LocalDate createTime;
}
