package net.cdsunrise.ztyg.acquisition.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;
import com.serotonin.modbus4j.sero.io.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.meter.domain.MeterCommunication;
import net.cdsunrise.ztyg.acquisition.meter.domain.MeterElectricityData;
import net.cdsunrise.ztyg.acquisition.meter.domain.MeterHistoryData;
import net.cdsunrise.ztyg.acquisition.meter.domain.MeterRealtimeData;
import net.cdsunrise.ztyg.acquisition.meter.mapper.MeterCommunicationMapper;
import net.cdsunrise.ztyg.acquisition.meter.mapper.MeterElectricityDataMapper;
import net.cdsunrise.ztyg.acquisition.meter.mapper.MeterHistoryDataMapper;
import net.cdsunrise.ztyg.acquisition.meter.service.MeterService;
import net.cdsunrise.ztyg.acquisition.protocol.modbus.component.ModbusTcpComponent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sh
 * @date 2019-11-27 10:46
 */
@Service
@Slf4j
public class MeterServiceImpl implements MeterService {

    @Value("${gather.meter.ip}")
    private String ip;
    @Value("${gather.meter.port}")
    private Integer port;

    private final ModbusTcpComponent modbusTcpComponent;
    private final MeterCommunicationMapper meterCommunicationMapper;
    private final MeterHistoryDataMapper meterHistoryDataMapper;
    private final MeterElectricityDataMapper meterElectricityDataMapper;
    private final BaseInfoMapper baseInfoMapper;

    public MeterServiceImpl(MeterCommunicationMapper meterCommunicationMapper, MeterHistoryDataMapper meterHistoryDataMapper,
                            ModbusTcpComponent modbusTcpComponent, MeterElectricityDataMapper meterElectricityDataMapper,
                            BaseInfoMapper baseInfoMapper) {
        this.meterCommunicationMapper = meterCommunicationMapper;
        this.meterHistoryDataMapper = meterHistoryDataMapper;
        this.modbusTcpComponent = modbusTcpComponent;
        this.meterElectricityDataMapper = meterElectricityDataMapper;
        this.baseInfoMapper = baseInfoMapper;
    }

    @Override
    public void pullAll() {
        List<MeterCommunication> meterCommunications = meterCommunicationMapper.selectList(null);
        if (meterCommunications != null && meterCommunications.size() > 0) {
            for (MeterCommunication meterCommunication : meterCommunications) {
                MeterHistoryData data = new MeterHistoryData();
                doPull(data, meterCommunication);
                power(data, meterCommunication);
                data.setCreateTime(LocalDateTime.now());
                MeterElectricityData electricity = getElectricity(meterCommunication);
                if (electricity != null) {
                    data.setOriginalElectricity(electricity.getOriginalElectricity());
                    // 当前采集电量=原始电量 - 前一条数据原始电量
                    MeterHistoryData meterHistoryData = selectOne(meterCommunication);
                    if (meterHistoryData == null) {
                        // 数据库第一条数据的当前用电量默认为0
                        data.setElectricity(0);
                    } else {
                        data.setElectricity(electricity.getOriginalElectricity() - meterHistoryData.getOriginalElectricity());
                    }
                }
                log.info("[MeterServiceImpl] insert meter history data [{}]", data);
                meterHistoryDataMapper.insert(data);
            }
        }
    }

    @Override
    public void pullElectricity() {
        List<MeterCommunication> meterCommunications = meterCommunicationMapper.selectList(null);
        if (meterCommunications != null && meterCommunications.size() > 0) {
            for (MeterCommunication meterCommunication : meterCommunications) {
                MeterElectricityData data = getElectricity(meterCommunication);
                log.info("[MeterServiceImpl] insert electricity data is [{}]", data);
                if (data != null) {
                    // 当前采集电量=原始电量 - 前一条数据原始电量
                    QueryWrapper<MeterElectricityData> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(MeterElectricityData::getDeviceCode, meterCommunication.getDeviceCode())
                            .orderByDesc(MeterElectricityData::getId).last("limit 1");
                    MeterElectricityData meterElectricityData = meterElectricityDataMapper.selectOne(queryWrapper);
                    if (meterElectricityData == null) {
                        data.setElectricity(data.getOriginalElectricity());
                    } else {
                        data.setElectricity(data.getOriginalElectricity() - meterElectricityData.getOriginalElectricity());
                    }
                    meterElectricityDataMapper.insert(data);
                }
            }
        }
    }

    @Override
    public MeterRealtimeData pullByDeviceCode(String deviceCode) {
        Asserts.assertStringNotNull(deviceCode, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        QueryWrapper<MeterCommunication> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MeterCommunication::getDeviceCode, deviceCode);
        MeterCommunication meterCommunication = meterCommunicationMapper.selectOne(queryWrapper);
        MeterHistoryData data = new MeterHistoryData();
        doPull(data, meterCommunication);
        power(data, meterCommunication);
        MeterRealtimeData realtimeData = new MeterRealtimeData();
        BeanUtils.copyProperties(data, realtimeData);
        MeterElectricityData electricity = getElectricity(meterCommunication);
        if (electricity != null) {
            realtimeData.setOriginalElectricity(electricity.getOriginalElectricity());
        }
        return realtimeData;
    }

    @Override
    public Integer getRealtimeElectricity(LocalDateTime start, LocalDateTime end) {
        QueryWrapper<MeterHistoryData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(MeterHistoryData::getCreateTime, start, end);
        List<MeterHistoryData> list = meterHistoryDataMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            Integer result = 0;
            for (MeterHistoryData meterHistoryData : list) {
                result += meterHistoryData.getElectricity();
            }
            return result;
        }
        return 0;
    }

    @Override
    public Map<Short, Integer> floorElectricity() {
        List<MeterCommunication> meterCommunications = meterCommunicationMapper.selectList(null);
        if (meterCommunications != null && meterCommunications.size() > 0) {
            Map<Short, Integer> map = new HashMap<>();
            for (MeterCommunication meterCommunication : meterCommunications) {
                MeterHistoryData meterHistoryData = selectOne(meterCommunication);
                if (meterHistoryData != null) {
                    String deviceCode = meterHistoryData.getDeviceCode();
                    BaseInfo baseInfo = baseInfo(deviceCode);
                    Integer oldValue = map.getOrDefault(baseInfo.getFloor(), 0);
                    Integer newValue = oldValue + meterHistoryData.getOriginalElectricity();
                    map.put(baseInfo.getFloor(), newValue);
                }
            }
            return map;
        }
        return null;
    }

    @Override
    public Map<String, Integer> electricityOfDay(LocalDate start, LocalDate end) {
        QueryWrapper<MeterElectricityData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(MeterElectricityData::getCreateTime, start, end);
        List<MeterElectricityData> meterElectricityData = meterElectricityDataMapper.selectList(queryWrapper);
        if (meterElectricityData != null && meterElectricityData.size() > 0) {
            Map<String, Integer> map = new HashMap<>();
            for (MeterElectricityData data : meterElectricityData) {
                map.put(data.getCreateTime().toString(), data.getElectricity());
            }
            return map;
        }
        return null;
    }

    private void doPull(MeterHistoryData data, MeterCommunication meterCommunication) {
        int voltageStartOffset = Integer.parseInt(meterCommunication.getVoltageRegisterId(), 16);
        // 查询11个寄存器
        int len = Integer.parseInt("000B", 16);
        String s = getResponse(meterCommunication.getSlaveId(), voltageStartOffset, len);
        log.info("[MeterServiceImpl] get voltage data: HEX is [{}]", s);
        if (!StringUtil.isEmpty(s) && s.length() == 44) {
            // 获取相电压
            BigDecimal voltageCoefficient = new BigDecimal(10);
            data.setUa(new BigDecimal(Integer.parseInt(s.substring(0, 4), 16)).divide(voltageCoefficient, 1, RoundingMode.UP).doubleValue());
            data.setUb(new BigDecimal(Integer.parseInt(s.substring(4, 8), 16)).divide(voltageCoefficient, 1, RoundingMode.UP).doubleValue());
            data.setUc(new BigDecimal(Integer.parseInt(s.substring(8, 12), 16)).divide(voltageCoefficient, 1, RoundingMode.UP).doubleValue());
            // 获取相电流，计算公式：(高位十进制*65535 + 低位十进制) / 1000
            BigDecimal currentFactory = new BigDecimal(1000);
            // 计算Ia相电流
            int highIa = Integer.parseInt(s.substring(12, 16), 16);
            int lowIa = Integer.parseInt(s.substring(16, 20), 16);
            int a = highIa * 65535 + lowIa;
            data.setIa(new BigDecimal(a).divide(currentFactory, 3, RoundingMode.UP).doubleValue());
            // 计算Ib相电流
            int highIb = Integer.parseInt(s.substring(20, 24), 16);
            int lowIb = Integer.parseInt(s.substring(24, 28), 16);
            int b = highIb * 65535 + lowIb;
            data.setIb(new BigDecimal(b).divide(currentFactory, 3, RoundingMode.UP).doubleValue());
            // 计算Ic相电流
            int highIc = Integer.parseInt(s.substring(28, 32), 16);
            int lowIc = Integer.parseInt(s.substring(32, 36), 16);
            int c = highIc * 65535 + lowIc;
            data.setIc(new BigDecimal(c).divide(currentFactory, 3, RoundingMode.UP).doubleValue());
            // 计算有功公里处
            int highPower = Integer.parseInt(s.substring(36, 40), 16);
            int lowPower = Integer.parseInt(s.substring(40, 44), 16);
            int power = highPower + lowPower;
            data.setPPower(power);
        }
    }

    /**
     * 采集功率因数和频率
     * @param data 采集数据
     * @param meterCommunication 通信基参数
     */
    private void power(MeterHistoryData data, MeterCommunication meterCommunication) {
        int voltageStartOffset = Integer.parseInt(meterCommunication.getVoltageRegisterId(), 16);
        // 查询2个寄存器
        int len = Integer.parseInt("0002", 16);
        String s = getResponse(meterCommunication.getSlaveId(), voltageStartOffset, len);
        log.info("[MeterServiceImpl] get power data: HEX is [{}]", s);
        if (!StringUtil.isEmpty(s) && s.length() == 8) {
            // 获取功率因数
            BigDecimal powerCoefficient = new BigDecimal(10000);
            data.setPowerFactory(new BigDecimal(Integer.parseInt(s.substring(0, 4), 16)).divide(powerCoefficient, 4, RoundingMode.UP).doubleValue());
            // 获取频率
            BigDecimal frequencyCoefficient = new BigDecimal(100);
            data.setFrequency(new BigDecimal(Integer.parseInt(s.substring(4, 8), 16)).divide(frequencyCoefficient, 4, RoundingMode.UP).doubleValue());
        }
    }

    private MeterElectricityData getElectricity(MeterCommunication meterCommunication) {
        int startOffset = Integer.parseInt(meterCommunication.getElectricityRegisterId(), 16);
        // 查询2个寄存器
        int len = Integer.parseInt("0002", 16);
        String s = getResponse(meterCommunication.getSlaveId(), startOffset, len);
        log.info("[MeterServiceImpl] get electricity data: HEX is [{}]", s);
        if (!StringUtil.isEmpty(s) && s.length() == 8) {
            int high = Integer.parseInt(s.substring(0, 4));
            int low = Integer.parseInt(s.substring(4, 8));
            int value = high * 65535 + low;
            MeterElectricityData data = new MeterElectricityData();
            data.setOriginalElectricity(value);
            data.setDeviceCode(meterCommunication.getDeviceCode());
            data.setCreateTime(LocalDate.now());
            return data;
        }
        return null;
    }

    private String getResponse(int slaveId, int offset, int len) {
        ModbusMaster master = modbusTcpComponent.getMaster(ip, port);
        ReadInputRegistersResponse response = modbusTcpComponent.readInputRegisters(master, slaveId, offset, len);
        if (response.isException()) {
            throw new BusinessException(String.valueOf(response.getExceptionCode()), response.getExceptionMessage());
        }
        return StreamUtils.dumpHex(response.getData());
    }

    private MeterHistoryData selectOne(MeterCommunication meterCommunication) {
        QueryWrapper<MeterHistoryData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MeterHistoryData::getDeviceCode, meterCommunication.getDeviceCode())
                .orderByDesc(MeterHistoryData::getId).last("limit 1");
        return meterHistoryDataMapper.selectOne(queryWrapper);
    }

    private BaseInfo baseInfo(String deviceCode) {
        QueryWrapper<BaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseInfo::getDeviceCode, deviceCode);
        return baseInfoMapper.selectOne(queryWrapper);
    }
}
