package net.cdsunrise.ztyg.acquisition.sensor.domain;

import lombok.Data;

/**
 * 温度实时数据
 * @author Binke Zhang
 * @date 2019/12/3 9:51
 */
@Data
public class TemperatureRealtimeData extends SensorRealtimeData{
    /**
     * 室内温度1
     */
    private Integer temperature;
}
