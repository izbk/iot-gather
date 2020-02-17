package net.cdsunrise.ztyg.acquisition.ils.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsDeviceState;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsPmCurve;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsThCurve;
import net.cdsunrise.ztyg.acquisition.ils.mapper.IlsPmCurveMapper;
import net.cdsunrise.ztyg.acquisition.ils.mapper.IlsThCurveMapper;
import net.cdsunrise.ztyg.acquisition.ils.service.DataProcessService;
import net.cdsunrise.ztyg.acquisition.ils.service.ExecuteService;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsRealtimeData;
import net.cdsunrise.ztyg.acquisition.ils.vo.RequestVo;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.component.RS485Component;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Binke Zhang
 * @date 2019/12/2 13:41
 */
@Service
public class ExecuteServiceImpl implements ExecuteService {
    @Autowired
    private IlsThCurveMapper thCurveMapper;
    @Autowired
    private IlsPmCurveMapper pmCurveMapper;
    @Autowired
    private DataProcessService dataProcessService;
    @Autowired
    private RS485Component component;
    @Override
    public List<IlsDeviceState> execute(RequestVo requestVo) {
        Asserts.assertNotNull(requestVo.getDeviceCode(),()-> new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"deviceCode不能为空"));
        Asserts.assertNotNull(requestVo.getCommand(),()-> new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"command不能为空"));
        RS485Msg reqMsg = dataProcessService.processCommand(requestVo.getDeviceCode(),requestVo.getCommand());
        RS485Msg respMsg = component.sendForIls(reqMsg);
        Asserts.assertNotNull(respMsg,()-> new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"执行命令失败，请稍后再试"));
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {}
        return dataProcessService.queryDeviceState(reqMsg);
    }

    @Override
    public List<IlsDeviceState> queryDeviceStates(String floor, String room) {
        if(StringUtils.isNotEmpty(room) && StringUtils.isEmpty(floor)){
            new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"room不为空时，floor不能为空");
        }
        return dataProcessService.queryDeviceState(floor, room);
    }

    @Override
    public List<IlsDeviceState> queryDeviceStates(String deviceCode) {
        Asserts.assertNotNull(deviceCode,()-> new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"deviceCode不能为空"));
        return dataProcessService.queryDeviceState(deviceCode);
    }

    @Override
    public IlsRealtimeData query(String deviceCode) {
        Asserts.assertNotNull(deviceCode,()-> new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"deviceCode不能为空"));
        return dataProcessService.queryEnvironmentState(deviceCode);
    }

    /**
     * 获取设备历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<IlsThCurve> queryThCurve(String deviceCode, Date startTime, Date endTime) {
        return thCurveMapper.selectList(new QueryWrapper<IlsThCurve>().lambda()
                .eq(IlsThCurve::getDeviceCode,deviceCode)
                .between(IlsThCurve::getCreateTime,startTime,endTime));
    }

    /**
     * 获取设备历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<IlsPmCurve> queryPmCurve(String deviceCode, Date startTime, Date endTime) {
        return pmCurveMapper.selectList(new QueryWrapper<IlsPmCurve>().lambda()
                .eq(IlsPmCurve::getDeviceCode,deviceCode)
                .between(IlsPmCurve::getCreateTime,startTime,endTime));
    }

}
