package net.cdsunrise.ztyg.acquisition.meter.domain;

import lombok.Data;

/**
 * @author sh
 * @date 2019-11-28 11:54
 */
@Data
public class MeterRealtimeData {
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
}
