package net.cdsunrise.ztyg.acquisition.vrv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.serotonin.modbus4j.sero.io.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.common.enums.DeviceTypeEnum;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.RS485Component;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.utils.Crc16Util;
import net.cdsunrise.ztyg.acquisition.vrv.domain.dto.RealDataDTO;
import net.cdsunrise.ztyg.acquisition.vrv.domain.dto.VrvCommunicationDTO;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.req.WriteRequest;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.resp.IndoorStateResp;
import net.cdsunrise.ztyg.acquisition.vrv.enums.*;
import net.cdsunrise.ztyg.acquisition.vrv.mapper.RealDataMapper;
import net.cdsunrise.ztyg.acquisition.vrv.mapper.VrvCommunicationMapper;
import net.cdsunrise.ztyg.acquisition.vrv.service.IDeviceControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备查询及控制服务
 *
 * @author Binke Zhang
 * @date 2019/11/15 11:55
 */
@Slf4j
@Service
public class DeviceControlServiceImpl implements IDeviceControlService {

    private final RS485Component rs485Component;
    private final VrvCommunicationMapper vrvCommunicationMapper;
    private final BaseInfoMapper baseInfoMapper;
    private final RealDataMapper realDataMapper;

    @Autowired
    public DeviceControlServiceImpl(RS485Component rs485Component, VrvCommunicationMapper vrvCommunicationMapper,
                                    BaseInfoMapper baseInfoMapper, RealDataMapper realDataMapper) {
        this.rs485Component = rs485Component;
        this.vrvCommunicationMapper = vrvCommunicationMapper;
        this.baseInfoMapper = baseInfoMapper;
        this.realDataMapper = realDataMapper;
    }

    @Value("${gather.vrv.ip}")
    private String ip;
    @Value("${gather.vrv.port}")
    private String port;

    @Override
    public boolean setFacilityAll(WriteRequest.SetAllReq req) {
        VrvCommunicationDTO result = getVrvCommunication(req.getDeviceCode());
        // 功能码
        String functionCode = "06";
        // 从机地址
        String slaveId = result.getSlaveId();
        // 寄存器起始地址
        String startOffset = result.getAllSetAddr();
        String value = "000306" + req.getWindSpeedEnum().getCode() + req.getPowerOnOffEnum().getCode() + req.getModeEnum().getCode()
                + fillZero(Integer.toHexString(req.getTemperature() * 10), 4).toUpperCase();

        String response = getResponse(slaveId, functionCode, startOffset, value);
        log.info("[DeviceControlServiceImpl] set all response: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return response.substring(8, 12).equals("0003");
    }

    @Override
    public AdapterStateEnum getAdapterState() {
        // 功能码
        String functionCode = "04";
        // 从机地址
        String slaveId = "01";
        // 寄存器起始地址
        String startOffset = "0000";
        // 查询4个寄存器
        String len = "0001";
        String response = getResponse(slaveId, functionCode, startOffset, len);
        log.info("[DeviceControlServiceImpl] get adapter state: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return AdapterStateEnum.getStateEnumMap().get(response.substring(6, 10));
    }

    @Override
    public boolean[] getIndoorMachineConnState() {
        // 功能码
        String functionCode = "04";
        // 从机地址
        String slaveId = "01";
        // 寄存器起始地址
        String startOffset = "0001";
        // 查询4个寄存器
        String len = "0004";
        String response = getResponse(slaveId, functionCode, startOffset, len);
        log.info("[DeviceControlServiceImpl] get indoor conn state: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return convertToBooleans(response.substring(6, 22).getBytes());
    }

    @Override
    public IndoorStateResp getIndoorMachineState(String deviceCode) {
        return getIndoorStateRestByDeviceCode(deviceCode, 0);
    }

    @Override
    public boolean setPowerAndWindSpeed(String deviceCode, SwitchWindEnum switchWind) {
        String response = doSetPowerAndWindSpeed(deviceCode, switchWind);
        log.info("[DeviceControlServiceImpl] set power and wind_speed response: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return response.substring(8, 12).equals(switchWind.getCode());
    }

    @Override
    public boolean setMode(String deviceCode, ModeEnum mode) {
        VrvCommunicationDTO result = getVrvCommunication(deviceCode);
        // 功能码
        String functionCode = "06";
        // 从机地址
        String slaveId = result.getSlaveId();
        String value = "00" + mode.getCode();
        String startOffset = result.getModeSetAddr();
        String response = getResponse(slaveId, functionCode, startOffset, value);
        log.info("[DeviceControlServiceImpl] set mode response: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return response.substring(8, 12).equals("00" + mode.getCode());
    }

    @Override
    public boolean setTemperature(String deviceCode, Integer temperature) {
        VrvCommunicationDTO result = getVrvCommunication(deviceCode);
        // 功能码
        String functionCode = "06";
        // 从机地址
        String slaveId = result.getSlaveId();
        String startOffset = result.getTemperatureSetAddr();
        String value = fillZero(Integer.toHexString(temperature * 10), 4).toUpperCase();
        String response = getResponse(slaveId, functionCode, startOffset, value);
        log.info("[DeviceControlServiceImpl] set temperature response: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        return response.substring(8, 12).equals(value);
    }

    @Override
    public Map<Short, List<IndoorStateResp>> getAllIndoorMachineState() {
        QueryWrapper<BaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseInfo::getDeviceType, DeviceTypeEnum.KT.getCode());
        List<BaseInfo> baseInfos = baseInfoMapper.selectList(queryWrapper);
        if (baseInfos != null && baseInfos.size() > 0) {
            Map<Short, List<IndoorStateResp>> map = new HashMap<>();
            for (BaseInfo baseInfo : baseInfos) {
                List<IndoorStateResp> list = map.getOrDefault(baseInfo.getFloor(), new ArrayList<>());
                IndoorStateResp resp = getIndoorStateRestByDeviceCode(baseInfo.getDeviceCode(), 0);
                list.add(resp);
                map.put(baseInfo.getFloor(), list);
            }
            return map;
        }
        return null;
    }

    @Override
    public List<IndoorStateResp> getIndoorMachineStateByFloor(Short floor) {
        List<BaseInfo> baseInfos = getBaseInfoByFloor(floor);
        if (baseInfos != null && baseInfos.size() > 0) {
            List<IndoorStateResp> list = new ArrayList<>();
            for (BaseInfo baseInfo : baseInfos) {
                IndoorStateResp resp = getIndoorStateRestByDeviceCode(baseInfo.getDeviceCode(), 0);
                list.add(resp);
            }
            return list;
        }
        return null;
    }

    @Override
    public void switchAllMachine(SwitchWindEnum switchWindEnum) {
        // 获取所有空调设备编码
        List<VrvCommunicationDTO> communicationDTOList = vrvCommunicationMapper.selectList(null);
        if (communicationDTOList != null && communicationDTOList.size() > 0) {
            // 如果是对空调全开，每个空调执行之后延迟 1s ，如果是全关延迟50ms
            boolean isOn = switchWindEnum == SwitchWindEnum.ON;
            try {
                for (VrvCommunicationDTO vrvCommunicationDTO : communicationDTOList) {
                    doSetPowerAndWindSpeed(vrvCommunicationDTO.getDeviceCode(), switchWindEnum);
                    if (isOn) {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void switchMachineByFloor(Short floor, SwitchWindEnum switchWindEnum) {
        List<BaseInfo> baseInfos = getBaseInfoByFloor(floor);
        if (baseInfos != null && baseInfos.size() > 0) {
            boolean isOn = switchWindEnum == SwitchWindEnum.ON;
            try {
                for (BaseInfo baseInfo : baseInfos) {
                    doSetPowerAndWindSpeed(baseInfo.getDeviceCode(), switchWindEnum);
                    if (isOn) {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public List<RealDataDTO> getRealDataByDeviceCode(String deviceCode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.toLocalDate(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);
        QueryWrapper<RealDataDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RealDataDTO::getDeviceCode, deviceCode)
                .between(RealDataDTO::getCreateTime, start, end);
        return realDataMapper.selectList(queryWrapper);
    }

    /**
     * 每个五分钟采集一次空调数据，不采集-1楼数据
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void acquisitionVrv() {
        QueryWrapper<BaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseInfo::getDeviceType, DeviceTypeEnum.KT.getCode())
                .ne(BaseInfo::getFloor, -1);
        List<BaseInfo> baseInfos = baseInfoMapper.selectList(queryWrapper);
        if (baseInfos != null && baseInfos.size() > 0) {
            for (BaseInfo baseInfo : baseInfos) {
                IndoorStateResp resp = getIndoorStateRestByDeviceCode(baseInfo.getDeviceCode(), 0);
                RealDataDTO dto = new RealDataDTO();
                dto.setDeviceCode(baseInfo.getDeviceCode());
                dto.setRunningMode(resp.getMode().getMessage());
                dto.setAirVolume(resp.getWindSpeed().getMessage());
                dto.setSetTemperature(resp.getSetTemperature());
                dto.setIndoorTemperature(resp.getIndoorTemperature());
                dto.setCreateTime(LocalDateTime.now());
                realDataMapper.insert(dto);
            }
        }
    }

    private List<BaseInfo> getBaseInfoByFloor(Short floor) {
        QueryWrapper<BaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseInfo::getDeviceType, DeviceTypeEnum.KT.getCode())
                .eq(BaseInfo::getFloor, floor);
        return baseInfoMapper.selectList(queryWrapper);
    }

    private String doSetPowerAndWindSpeed(String deviceCode, SwitchWindEnum switchWind) {
        VrvCommunicationDTO result = getVrvCommunication(deviceCode);
        // 功能码
        String functionCode = "06";
        // 从机地址
        String slaveId = result.getSlaveId();
        String startOffset = result.getSwitchWindSetAddr();
        String value = switchWind.getCode();
        return getResponse(slaveId, functionCode, startOffset, value);
    }

    private IndoorStateResp getIndoorStateRestByDeviceCode(String deviceCode, int num) {
        VrvCommunicationDTO result = getVrvCommunication(deviceCode);
        Asserts.assertNotNull(result, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        // 功能码
        String functionCode = "04";
        // 从机地址
        String slaveId = result.getSlaveId();
        // 寄存器起始地址
        String startOffset = result.getQueryIndoorStateAddr();
        // 查询6个寄存器
        String len = "0006";
        String response = getResponse(slaveId, functionCode, startOffset, len);
        log.info("[DeviceControlServiceImpl] get indoor machine state: HEX is [{}]", response);
        Asserts.assertNotNull(response, () -> new BusinessException(ExceptionEnum.OPERATE_FAIL));
        // 如果获取的结果不对，重新再获取一次
        if (response.length() != 30) {
            // 根据num判断，最多只能递归调用三次，防止硬件网关出问题出现死循环
            num++;
            if (num > 2) {
                return null;
            }
            getIndoorStateRestByDeviceCode(deviceCode, num);
        }
        String s = response.substring(6, 30);
        IndoorStateResp resp = new IndoorStateResp();
        if (!StringUtil.isEmpty(s) && s.length() == 24) {
            resp.setDeviceCode(deviceCode);
            resp.setWindSpeed(WindSpeedEnum.getWindSpeedEnumMap().get(s.substring(0, 2)));
            SwitchEnum switchEnum = SwitchEnum.getSwitchEnumMap().get(s.substring(2, 4));
            resp.setSwitchStr(switchEnum == null ? SwitchEnum.OFF : switchEnum);
            resp.setCleanDesc(FilterCleanEnum.getFilterCleanEnumMap().get(s.substring(4, 6)));
            resp.setMode(ModeEnum.getModeEnumMap().get(s.substring(6, 8)));
            resp.setSetTemperature(Integer.parseInt(s.substring(8, 12), 16) / 10);
            resp.setIndoorTemperature(Integer.parseInt(s.substring(16, 20), 16) / 10);
            resp.setTpState(TPStateEnum.getTpStateEnumMap().get(s.substring(20, 24)));
            resp.setLocationDesc(result.getLocationDesc());
        }
        return resp;
    }

    private VrvCommunicationDTO getVrvCommunication(String deviceCode) {
        QueryWrapper<VrvCommunicationDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VrvCommunicationDTO::getDeviceCode, deviceCode);
        VrvCommunicationDTO result = vrvCommunicationMapper.selectOne(queryWrapper);
        Asserts.assertNotNull(result, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        return result;
    }

    private String getResponse(String slaveId, String functionCode, String startOffset, String value) {
        String msg = slaveId + functionCode + startOffset + value;
        String crc = Crc16Util.getCRC(StreamUtils.fromHex(msg));
        return rs485Component.sendForVrv(msg+crc);
    }

    private boolean[] convertToBooleans(byte[] data) {
        boolean[] bdata = new boolean[data.length * 8];

        for(int i = 0; i < bdata.length; ++i) {
            bdata[i] = (data[i / 8] >> i % 8 & 1) == 1;
        }
        return bdata;
    }

    /**
     *
     * @param str 字符串
     * @param len 填充之后的字符长度
     * @return 填充之后的字符
     */
    private String fillZero(String str, int len) {
        if (str == null || str.length() == len || str.length() > len) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0, l1 = len - sb.length(); i < l1; i++) {
            sb.insert(0, "0");
        }
        str = sb.toString();
        return str;
    }
}
