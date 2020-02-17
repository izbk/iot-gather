package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.dahua.module.DeviceInitModule;
import net.cdsunrise.ztyg.acquisition.dahua.module.DeviceSearchModule;
import net.cdsunrise.ztyg.acquisition.dahua.vo.DeviceInfoVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备搜索与初始化服务
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class DeviceSearchAndInitService {

    /*
     *  设备组播和广播搜索回调
     */
    private SearchDevicesCallback callback = new SearchDevicesCallback();

    /**
     * 初始化设备
     * @param szMac
     * @param password
     * @param cellPhone_mail
     * @param byPwdResetWay
     * @return
     */
    public boolean initDevAccount(String szMac, String password, String cellPhone_mail, byte byPwdResetWay){
        return DeviceInitModule.initDevAccount(szMac, password, cellPhone_mail, byPwdResetWay);
    }

    /**
     *  设备组播和广播搜索
     * @param cbSearchDevices
     * @return
     */
    public NetSDKLib.LLong multiBroadcastDeviceSearch(NetSDKLib.fSearchDevicesCB cbSearchDevices){
        return DeviceSearchModule.multiBroadcastDeviceSearch(cbSearchDevices);
    }

    /**
     * 停止设备组播和广播搜索
     * @param m_DeviceSearchResult
     */
    public void stopDeviceSearch(NetSDKLib.LLong m_DeviceSearchResult){
        DeviceSearchModule.stopDeviceSearch(m_DeviceSearchResult);
    }

    /**
     * 设备IP单播搜索
     * @param startIP 起始IP
     * @param nIpNum IP个数，最大 256
     * @param cbSearchDevicesCB 回调函数
     * @return
     */
    public boolean unicastDeviceSearch(String startIP, int nIpNum, NetSDKLib.fSearchDevicesCB cbSearchDevicesCB){
        return DeviceSearchModule.unicastDeviceSearch(startIP, nIpNum, cbSearchDevicesCB);
    }

    private class SearchDevicesCallback implements NetSDKLib.fSearchDevicesCB {
        @Override
        public void invoke(Pointer pDevNetInfo, Pointer pUserData) {
            NetSDKLib.DEVICE_NET_INFO_EX deviceInfo =  new NetSDKLib.DEVICE_NET_INFO_EX();
            ToolKits.GetPointerData(pDevNetInfo, deviceInfo);
            // 获取设备数据 需测试每次返回的数据行数

        }
    }

    /**
     * 解析设备数据为列表
     * @param deviceInfo
     * @return
     */
    private List parseDeviceDataToList(NetSDKLib.DEVICE_NET_INFO_EX deviceInfo){
        List<String> macList = new ArrayList<>();
        List<DeviceInfoVo> deviceInfoVoList = new ArrayList<>();
        parseDeviceData(macList, deviceInfoVoList, deviceInfo);
        return deviceInfoVoList;
    }

    /**
     * 解析单条设备数据
     * @param macList
     * @param deviceInfoVoList
     * @param deviceInfo
     */
    private void parseDeviceData(List<String> macList,List<DeviceInfoVo> deviceInfoVoList,NetSDKLib.DEVICE_NET_INFO_EX deviceInfo){
        if(!macList.contains(new String(deviceInfo.szMac))) {
            macList.add(new String(deviceInfo.szMac));
            DeviceInfoVo deviceInfoVo = new DeviceInfoVo();

            // 初始化状态
            deviceInfoVo.setInitStatus(Res.string().getInitStateInfo(deviceInfo.byInitStatus & 0x03));
            // IP版本
            deviceInfoVo.setIpVersion("IPV" + String.valueOf(deviceInfo.iIPVersion));
            // IP
            if(!new String(deviceInfo.szIP).trim().isEmpty()) {
                deviceInfoVo.setIp(new String(deviceInfo.szIP).trim());
            } else {
                deviceInfoVo.setIp("");
            }
            // 端口号
            deviceInfoVo.setPort(String.valueOf(deviceInfo.nPort));
            // 子网掩码
            if(!new String(deviceInfo.szSubmask).trim().isEmpty()) {
                deviceInfoVo.setSubnetMask(new String(deviceInfo.szSubmask).trim());
            } else {
                deviceInfoVo.setSubnetMask("");
            }

            // 网关
            if(!new String(deviceInfo.szGateway).trim().isEmpty()) {
                deviceInfoVo.setGateway(new String(deviceInfo.szGateway).trim());
            } else {
                deviceInfoVo.setGateway("");
            }

            // MAC地址
            if(!new String(deviceInfo.szMac).trim().isEmpty()) {
                deviceInfoVo.setMac(new String(deviceInfo.szMac).trim());
            } else {
                deviceInfoVo.setMac("");
            }

            // 设备类型
            if(!new String(deviceInfo.szDeviceType).trim().isEmpty()) {
                deviceInfoVo.setDeviceType(new String(deviceInfo.szDeviceType).trim());
            } else {
                deviceInfoVo.setDeviceType("");
            }

            // 详细类型
            if(!new String(deviceInfo.szNewDetailType).trim().isEmpty()) {
                deviceInfoVo.setDetailType(new String(deviceInfo.szNewDetailType).trim());
            } else {
                deviceInfoVo.setDetailType("");
            }

            // HTTP端口号
            deviceInfoVo.setHttpPort(String.valueOf(deviceInfo.nHttpPort));

            // 将MAC地址   跟 密码重置方式，放进容器
            deviceInfoVo.setResetPasswordWay(deviceInfo.byPwdResetWay);

            deviceInfoVoList.add(deviceInfoVo);
        }
    }

}
