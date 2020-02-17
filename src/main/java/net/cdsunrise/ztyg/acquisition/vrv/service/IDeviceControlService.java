package net.cdsunrise.ztyg.acquisition.vrv.service;

import net.cdsunrise.ztyg.acquisition.vrv.domain.dto.RealDataDTO;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.req.WriteRequest;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.resp.IndoorStateResp;
import net.cdsunrise.ztyg.acquisition.vrv.enums.AdapterStateEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.ModeEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.SwitchWindEnum;

import java.util.List;
import java.util.Map;

/**
 * 空调设备控制
 * @author Binke Zhang
 * @date 2019/11/15 11:52
 */
public interface IDeviceControlService {
    /**
     * 设置单个设备的所有控制参数:开关机及风速、制冷模式、温度
     * @param req
     * @return
     */
    boolean setFacilityAll(WriteRequest.SetAllReq req);

    /**
     * 查询适配器状态
     * @return 适配器状态
     */
    AdapterStateEnum getAdapterState();

    /**
     * 查询室内机连接状态
     * @return 状态
     */
    boolean[] getIndoorMachineConnState();

    /**
     * 查询室内机状态
     * @return
     */
    IndoorStateResp getIndoorMachineState(String deviceCode);

    /**
     * 开关机及风速设定
     * @param switchWind 具体操作
     * @return 是否设置成功
     */
    boolean setPowerAndWindSpeed(String deviceCode, SwitchWindEnum switchWind);

    /**
     * 模式设定
     * @param mode 模式
     * @return 是否设置成功
     */
    boolean setMode(String deviceCode, ModeEnum mode);

    /**
     * 温度设定
     * @param temperature 温度
     * @return 是否设置成功
     */
    boolean setTemperature(String deviceCode, Integer temperature);

    /**
     * 获取所有空调室内机状态，按楼层分
     * @return 所有空调室内机状态
     */
    Map<Short, List<IndoorStateResp>> getAllIndoorMachineState();

    /**
     * 按楼层获取空调状态
     * @param floor 楼层
     * @return 空调状态
     */
    List<IndoorStateResp> getIndoorMachineStateByFloor(Short floor);

    /**
     * 控制所有空调开关
     * @param switchWindEnum 具体操作，只针对开关
     */
    void switchAllMachine(SwitchWindEnum switchWindEnum);

    /**
     * 根据楼层控制开关
     * @param floor 楼层
     * @param switchWindEnum 具体操作，只针对开关
     */
    void switchMachineByFloor(Short floor, SwitchWindEnum switchWindEnum);

    /**
     * 根据设备编码获取空调采集数据
     * @param deviceCode 设备编码
     * @return 返回当天采集数据
     */
    List<RealDataDTO> getRealDataByDeviceCode(String deviceCode);
}
