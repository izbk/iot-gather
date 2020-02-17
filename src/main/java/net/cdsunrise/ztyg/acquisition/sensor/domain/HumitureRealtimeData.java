package net.cdsunrise.ztyg.acquisition.sensor.domain;

import lombok.Data;

/**
 * 温湿度实时数据
 * @author Binke Zhang
 * @date 2019/12/3 9:51
 */
@Data
public class HumitureRealtimeData extends SensorRealtimeData{
    /**
     * 温度
     */
    private Integer temperature;
    /**
     * 湿度
     */
    private Integer humidity;
}
