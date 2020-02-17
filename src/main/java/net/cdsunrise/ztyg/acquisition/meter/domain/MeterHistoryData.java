package net.cdsunrise.ztyg.acquisition.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sh
 * @date 2019-11-27 10:22
 */
@Data
@TableName("t_meter_history_data")
public class MeterHistoryData {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** A相相电压，单位V */
    private double ua;
    /** B相相电压，单位V */
    private double ub;
    /** C相相电压，单位V */
    private double uc;
    /** A相电流，单位A */
    private double ia;
    /** B相电流，单位A */
    private double ib;
    /** C相电流，单位A */
    private double ic;
    /** 有功功率，单位W */
    private int pPower;
    /** 功率因素 */
    private double powerFactory;
    /** 频率，单位HZ */
    private double frequency;
    /** 原始电量 */
    private Integer originalElectricity;
    /** 用电量，单位KWh */
    private Integer electricity;
    /** 采集时间 */
    private LocalDateTime createTime;
}
