package net.cdsunrise.ztyg.acquisition.sensor.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.common.enums.DeviceTypeEnum;
import net.cdsunrise.ztyg.acquisition.common.enums.SystemTypeEnum;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorCO2;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorHumidity;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorTemperature;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorCO2Mapper;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorHumidityMapper;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorTemperatureMapper;
import net.cdsunrise.ztyg.acquisition.sensor.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 定时监控服务状态
 * @author Binke Zhang
 * @date 2019/9/19 20:16
 */
@Slf4j
@Component
public class GatherSensorDataTask {

    private final CommonCache<String, List<BaseInfo>> sensorCache = CommonCache.getInstance();
    private static final String SENSOR_DEVICE = "SENSOR_DEVICE";

    @Autowired
    private BaseInfoMapper baseInfoMapper;
    @Autowired
    private SensorCO2Mapper sensorCO2Mapper;
    @Autowired
    private SensorTemperatureMapper sensorTemperatureMapper;
    @Autowired
    private SensorHumidityMapper sensorHumidityMapper;
    @Autowired
    private ProcessService processService;

    @ConditionalOnProperty(value = "schedule.enabled", havingValue = "true")
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduled(){
        List<BaseInfo> list = getSensorDevices();
        for (BaseInfo b: list) {
            DeviceTypeEnum deviceTypeEnum = DeviceTypeEnum.getDeviceTypeEnumMap().get(Integer.valueOf(b.getDeviceType()));
            switch(deviceTypeEnum){
                case CO2:
                    SensorCO2 sensorCO2 = processService.readCO2(b.getDeviceCode());
                    if(sensorCO2 != null){
                        sensorCO2Mapper.insert(sensorCO2);
                    }
                    break;
                case WD:
                    SensorTemperature sensorTemperature = processService.readTemperature(b.getDeviceCode());
                    if(sensorTemperature != null){
                        sensorTemperatureMapper.insert(sensorTemperature);
                    }
                    break;
                case SNWD:
                case SWWD:
                    SensorHumidity sensorHumidity = processService.readHumidity(b.getDeviceCode());
                    if(sensorHumidity != null){
                        sensorHumidityMapper.insert(sensorHumidity);
                    }
                default:
            }
        }
    }

    /**
     * 获取传感器设备信息
     * @return
     */
    private List<BaseInfo> getSensorDevices(){
        List<BaseInfo> list = sensorCache.get(SENSOR_DEVICE);
        if(list == null || list.size() == 0){
            list = baseInfoMapper.selectList(new QueryWrapper<BaseInfo>().lambda()
                    .eq(BaseInfo::getSystemType, SystemTypeEnum.SBJK.getCode())
                    .in(BaseInfo::getDeviceType, Arrays.asList(DeviceTypeEnum.CO2.getCode(),
                            DeviceTypeEnum.SNWD.getCode(),
                            DeviceTypeEnum.SWWD.getCode(),
                            DeviceTypeEnum.WD.getCode()))
            );
            sensorCache.put(SENSOR_DEVICE,list);
        }
        return list;
    }

}