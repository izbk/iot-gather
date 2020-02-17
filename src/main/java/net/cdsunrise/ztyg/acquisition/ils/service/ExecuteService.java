package net.cdsunrise.ztyg.acquisition.ils.service;

import net.cdsunrise.ztyg.acquisition.ils.domain.IlsDeviceState;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsPmCurve;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsThCurve;
import net.cdsunrise.ztyg.acquisition.ils.vo.EnvironmentResponseVo;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsRealtimeData;
import net.cdsunrise.ztyg.acquisition.ils.vo.QueryDeviceStateVo;
import net.cdsunrise.ztyg.acquisition.ils.vo.RequestVo;

import java.util.Date;
import java.util.List;

/**
 * @author Binke Zhang
 * @date 2019/12/2 11:59
 */
public interface ExecuteService {
    /**
     * 执行命令
     * @param requestVo
     * @return
     */
    List<IlsDeviceState> execute(RequestVo requestVo);

    /**
     * 查询设备状态
     * @param floor
     * @param room
     * @return
     */
    List<IlsDeviceState> queryDeviceStates(String floor, String room);

    /**
     * 查询设备状态
     * @param deviceSn
     * @return
     */
    List<IlsDeviceState> queryDeviceStates(String deviceSn);

    /**
     * 查询指令
     * @param deviceCode
     * @return
     */
    IlsRealtimeData query(String deviceCode);

    /**
     * 获取温湿度历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<IlsThCurve> queryThCurve(String deviceCode, Date startTime, Date endTime);

    /**
     * 获取pm25历史曲线
     * @param deviceCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<IlsPmCurve> queryPmCurve(String deviceCode, Date startTime, Date endTime);
}
