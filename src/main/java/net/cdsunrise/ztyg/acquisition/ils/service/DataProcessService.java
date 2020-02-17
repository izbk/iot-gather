package net.cdsunrise.ztyg.acquisition.ils.service;

import net.cdsunrise.ztyg.acquisition.ils.domain.IlsDeviceState;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsRealtimeData;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;

import java.util.List;

/**
 * 返回结果处理类
 * @author Binke Zhang
 * @date 2019/11/29 14:30
 */
public interface DataProcessService {

    /**
     * 解析设备指令
     * @param deviceCode
     * @param command
     * @return
     */
    RS485Msg processCommand(String deviceCode, Integer command);

    /**
     * 解析环境参数信息
     * @param rs485Msg
     * @return
     */
    void processEnvironmentMsg(RS485Msg rs485Msg);

    /**
     * 装载照明设备状态缓存
     */
    void loadDeviceState();

    /**
     * 加载楼层房间数据字段
     */
    void loadDataDictionary();

    /**
     * 更新设备状态
     * @param respMsg
     */
    void updateDeviceState(RS485Msg respMsg);

    /**
     * 获取设备状态
     * @param reqMsg
     * @return
     */
    List<IlsDeviceState> queryDeviceState(RS485Msg reqMsg);
    /**
     * 获取设备状态
     * @param floor
     * @param room
     * @return
     */
    List<IlsDeviceState> queryDeviceState(String floor, String room);

    /**
     * 获取环境信息
     */
    IlsRealtimeData queryEnvironmentState(String deviceCode);

    /**
     * 获取设备状态
     * @param deviceCode
     * @return
     */
    List<IlsDeviceState> queryDeviceState(String deviceCode);
    /**
     * 获取全局搜索指令
     * @return
     */
    RS485Msg getGlobalSearchCommand();

    /**
     * 加载照明系统缓存
     */
    void loadILSCache();

}
