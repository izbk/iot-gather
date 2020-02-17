package net.cdsunrise.ztyg.acquisition.sensor.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.protocol.modbus.component.ModbusTcpComponent;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.sensor.domain.*;
import net.cdsunrise.ztyg.acquisition.sensor.enums.SensorTypeEnum;
import net.cdsunrise.ztyg.acquisition.sensor.enums.StateEnum;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorCO2Mapper;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorCommunicationMapper;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorHumidityMapper;
import net.cdsunrise.ztyg.acquisition.sensor.mapper.SensorTemperatureMapper;
import net.cdsunrise.ztyg.acquisition.sensor.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:49
 */
@Service
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private ModbusTcpComponent modbusTcpComponent;
    @Autowired
    private SensorCommunicationMapper sensorCommunicationMapper;
    @Autowired
    private SensorCO2Mapper sensorCO2Mapper;
    @Autowired
    private SensorHumidityMapper sensorHumidityMapper;
    @Autowired
    private SensorTemperatureMapper sensorTemperatureMapper;

    private static final Integer SLAVE_ID = 1;
    private static final Integer COMMON_LEN = 2;
    private final CommonCache<String, Map<String,SensorCommunication>> sensorCommCache = CommonCache.getInstance();
    private static final String SENSOR_COMMUNICATION = "SENSOR_COMMUNICATION";

    @Override
    public SensorRealtimeData read(String deviceCode){
        Map<String,SensorCommunication> map = getSensorCommunications();
        SensorCommunication sensorCommunication = map.get(deviceCode);
        Asserts.assertNotNull(sensorCommunication,()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备通信信息失败"));
        ModbusMaster master = modbusTcpComponent.getMaster(sensorCommunication.getIp(),sensorCommunication.getPort());
        Float response;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Integer type = sensorCommunication.getType();
        SensorRealtimeData sensorRealtimeData = new SensorRealtimeData();
        sensorRealtimeData.setCommunicateState(StateEnum.FAULT.getCode());
        sensorRealtimeData.setRunningState(StateEnum.FAULT.getCode());
        sensorRealtimeData.setUpdateTime(new Date());
        /**
         * 获取实时数据
         */
        switch(SensorTypeEnum.getModeEnumMap().get(type)){
            case CO2:
                response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
                sensorRealtimeData.setSensorType(SensorTypeEnum.CO2.getCode());
                if(response != null){
                    sensorRealtimeData.setCo2Concentration(decimalFormat.format(response));
                    sensorRealtimeData.setCommunicateState(StateEnum.NORMAL.getCode());
                    sensorRealtimeData.setRunningState(StateEnum.NORMAL.getCode());
                }
                break;
            case WD:
                response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
                sensorRealtimeData.setSensorType(SensorTypeEnum.WD.getCode());
                if(response != null){
                    sensorRealtimeData.setTemperature(decimalFormat.format(response));
                    sensorRealtimeData.setCommunicateState(StateEnum.NORMAL.getCode());
                    sensorRealtimeData.setRunningState(StateEnum.NORMAL.getCode());
                }
                break;
            case WSD:
                // 读取温度值
                response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
                sensorRealtimeData.setSensorType(SensorTypeEnum.WSD.getCode());
                if(response != null){
                    sensorRealtimeData.setTemperature(decimalFormat.format(response));
                    // 读取湿度值
                    response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister()+COMMON_LEN, DataType.FOUR_BYTE_FLOAT);
                    sensorRealtimeData.setHumidity(decimalFormat.format(response));
                    sensorRealtimeData.setCommunicateState(StateEnum.NORMAL.getCode());
                    sensorRealtimeData.setRunningState(StateEnum.NORMAL.getCode());
                }
                break;
                default:
        }
        sensorRealtimeData.setUptime(sensorCommunication.getUptime());
        sensorRealtimeData.setRunningTime(DateUtil.between(sensorCommunication.getUptime(),sensorRealtimeData.getUpdateTime(), DateUnit.HOUR));
        return sensorRealtimeData;
    }


    @SuppressWarnings("Duplicates")
    @Override
    public SensorCO2 readCO2(String deviceCode){
        Map<String,SensorCommunication> map = getSensorCommunications();
        SensorCommunication sensorCommunication = map.get(deviceCode);
        Asserts.assertNotNull(sensorCommunication,()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备通信信息失败"));
        Asserts.assertTrue(SensorTypeEnum.CO2.getCode().equals(sensorCommunication.getType()),()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"设备类型信息错误"));
        ModbusMaster master = modbusTcpComponent.getMaster(sensorCommunication.getIp(),sensorCommunication.getPort());
        Float response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
        if(response != null){
            SensorCO2 sensorCO2 = new SensorCO2();
            sensorCO2.setDeviceCode(deviceCode);
            sensorCO2.setCo2Concentration(response);
            sensorCO2.setCreateTime(Calendar.getInstance().getTime());
            return sensorCO2;
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public SensorTemperature readTemperature(String deviceCode){
        Map<String,SensorCommunication> map = getSensorCommunications();
        SensorCommunication sensorCommunication = map.get(deviceCode);
        Asserts.assertNotNull(sensorCommunication,()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备通信信息失败"));
        Asserts.assertTrue(SensorTypeEnum.WD.getCode().equals(sensorCommunication.getType()),()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"设备类型信息错误"));
        ModbusMaster master = modbusTcpComponent.getMaster(sensorCommunication.getIp(),sensorCommunication.getPort());
        Float response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
        if(response != null){
            SensorTemperature sensorTemperature = new SensorTemperature();
            sensorTemperature.setDeviceCode(deviceCode);
            sensorTemperature.setTemperature(response);
            sensorTemperature.setCreateTime(Calendar.getInstance().getTime());
            return sensorTemperature;
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public SensorHumidity readHumidity(String deviceCode){
        Map<String,SensorCommunication> map = getSensorCommunications();
        SensorCommunication sensorCommunication = map.get(deviceCode);
        Asserts.assertNotNull(sensorCommunication,()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备通信信息失败"));
        Asserts.assertTrue(SensorTypeEnum.WSD.getCode().equals(sensorCommunication.getType()),()->new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"设备类型信息错误"));
        ModbusMaster master = modbusTcpComponent.getMaster(sensorCommunication.getIp(),sensorCommunication.getPort());
        Float response = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister(), DataType.FOUR_BYTE_FLOAT);
        if(response != null){
            SensorHumidity sensorHumidity = new SensorHumidity();
            sensorHumidity.setTemperature(response);
            // 读取湿度值
            Float humidity = (Float) modbusTcpComponent.readHoldingRegister(master,SLAVE_ID,sensorCommunication.getReadRegister()+COMMON_LEN, DataType.FOUR_BYTE_FLOAT);
            sensorHumidity.setHumidity(humidity);
            sensorHumidity.setDeviceCode(deviceCode);
            sensorHumidity.setCreateTime(Calendar.getInstance().getTime());
            return sensorHumidity;
        }
        return null;
    }

    /**
     *  获取通信信息
     * @return
     */
    private Map<String,SensorCommunication> getSensorCommunications(){
        Map<String,SensorCommunication> map = sensorCommCache.get(SENSOR_COMMUNICATION);
        if(map == null || map.size() == 0){
            List<SensorCommunication> list = sensorCommunicationMapper.selectList(new QueryWrapper<>());
            map = list.stream().collect(Collectors.toMap(SensorCommunication::getDeviceCode, account -> account));
            sensorCommCache.put(SENSOR_COMMUNICATION,map);
        }
        return map;
    }

    /**
     * 获取设备历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Map<String,Object> query(String deviceCode,Date startTime,Date endTime) {
        Map<String, SensorCommunication> map = getSensorCommunications();
        SensorCommunication sc = map.get(deviceCode);
        Asserts.assertNotNull(sc, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(), "设备编码不存在"));
        Map<String, Object> data = new HashMap<>();
        switch (SensorTypeEnum.getModeEnumMap().get(sc.getType())) {
            case CO2:
                data.put("type",SensorTypeEnum.CO2.toString());
                data.put(SensorTypeEnum.CO2.toString(),sensorCO2Mapper.selectList(new QueryWrapper<SensorCO2>().lambda()
                .eq(SensorCO2::getDeviceCode,deviceCode)
                .between(SensorCO2::getCreateTime,startTime,endTime)
                .orderByAsc(SensorCO2::getCreateTime)));
                break;
            case WD:
                data.put("type",SensorTypeEnum.WD.toString());
                data.put(SensorTypeEnum.WD.toString(),sensorTemperatureMapper.selectList(new QueryWrapper<SensorTemperature>().lambda()
                        .eq(SensorTemperature::getDeviceCode,deviceCode)
                        .between(SensorTemperature::getCreateTime,startTime,endTime)
                        .orderByAsc(SensorTemperature::getCreateTime)));
                break;
            case WSD:
                data.put("type",SensorTypeEnum.WSD.toString());
                data.put(SensorTypeEnum.WSD.toString(),sensorHumidityMapper.selectList(new QueryWrapper<SensorHumidity>().lambda()
                        .eq(SensorHumidity::getDeviceCode,deviceCode)
                        .between(SensorHumidity::getCreateTime,startTime,endTime)
                        .orderByAsc(SensorHumidity::getCreateTime)));
                break;
            default:
        }
        return data;
    }

}
