package net.cdsunrise.ztyg.acquisition.meter.service;

import net.cdsunrise.ztyg.acquisition.meter.domain.MeterRealtimeData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author sh
 * @date 2019-11-27 10:33
 */
public interface MeterService {
    /**
     * 拉取所有电表数据
     */
    void pullAll();

    /**
     * 拉取电表电量数据
     */
    void pullElectricity();

    /**
     * 拉取指定电表数据
     * @param deviceCode 设备编码
     * @return 电表数据
     */
    MeterRealtimeData pullByDeviceCode(String deviceCode);

    /**
     * 获取当天累积电量
     * @param start 开始时间
     * @param end 结束时间
     * @return 电量
     */
    Integer getRealtimeElectricity(LocalDateTime start, LocalDateTime end);

    /**
     * 获取各楼层电量
     * @return 楼层电量
     */
    Map<Short, Integer> floorElectricity();

    /**
     * 按时间查询每天电量
     * @param start 开始日期
     * @param end 结束日期
     * @return 该时间段每天电量
     */
    Map<String, Integer> electricityOfDay(LocalDate start, LocalDate end);
}
