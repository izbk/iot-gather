package net.cdsunrise.ztyg.acquisition.sensor.service;

import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorCO2;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorHumidity;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorRealtimeData;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorTemperature;

import java.util.Date;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:48
 */
public interface ProcessService {
    /**
     * 读取传感器
     * @param deviceCode
     * @return
     */
    SensorRealtimeData read(String deviceCode);

    /**
     * 获取CO2数据
     * @param deviceCode
     * @return
     */
    SensorCO2 readCO2(String deviceCode);
    /**
     * 获取温度数据
     * @param deviceCode
     * @return
     */
    SensorTemperature readTemperature(String deviceCode);
    /**
     * 获取温湿度数据
     * @param deviceCode
     * @return
     */
    SensorHumidity readHumidity(String deviceCode);

    /**
     * 获取历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Object> query(String deviceCode, Date startTime, Date endTime);
}
