package net.cdsunrise.ztyg.acquisition.sensor.domain;

import lombok.Data;

/**
 * CO2实时数据
 * @author Binke Zhang
 * @date 2019/12/3 9:51
 */
@Data
public class CO2RealtimeData extends SensorRealtimeData{
    /**
     * CO2浓度1
     */
    private Integer co2Concentration;
}
