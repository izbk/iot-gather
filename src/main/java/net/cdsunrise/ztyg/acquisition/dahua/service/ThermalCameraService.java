package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.dahua.module.ThermalCameraModule;
import net.cdsunrise.ztyg.acquisition.dahua.vo.ThermalInfoVo;
import net.cdsunrise.ztyg.acquisition.dahua.vo.ThermalItemInfoVo;
import net.cdsunrise.ztyg.acquisition.dahua.vo.ThermalPointInfoVo;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 热成像模块
 * @author Binke Zhang
 * @date 2019/8/7 10:39
 * @Copyright: izbk@163.com All rights reserved.
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ThermalCameraService {
    /**
     * 设备断线通知回调
     */
    private static DisConnectCallback disConnect  = new DisConnectCallback();
    /**
     * 网络连接恢复
     */
    private static HaveReConnectCallback haveReConnect = new HaveReConnectCallback();

    private String [] arrMeterType = Res.string().getMeterTypeList();
    private String [] arrTemperUnit = Res.string().getTemperUnitList();
    /**
     * 预览句柄
     */
    public static NetSDKLib.LLong m_hPlayHandleOne = new NetSDKLib.LLong(0);
    public static NetSDKLib.LLong m_hPlayHandleTwo = new NetSDKLib.LLong(0);

    /**
     * heatMap 功能使用的锁
     */
    private ReentrantLock lock = new ReentrantLock();
    private NetSDKLib.NET_RADIOMETRY_DATA gData = new NetSDKLib.NET_RADIOMETRY_DATA();
    private RadiometryAttachCB cbNotify = new RadiometryAttachCB();

    /**
     * 获取测温点信息
     * @param channel
     * @param x
     * @param y
     * @return 0:meterType 1:temperUnit 2:temper
     */
    public ThermalPointInfoVo queryPointInfo(int channel,short x,short y){
        NetSDKLib.NET_RADIOMETRYINFO pointInfo = ThermalCameraModule.queryPointTemper(channel, x, y);
        if (pointInfo == null) {
            log.error("获取测温点信息失败,code:{},message:{}",ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
            return null;
        }else{
            return parsePointData(pointInfo);
        }
    }

    /**
     * 提取测温点信息
     * @param pointInfo
     * @return String[] 0:meterType 1:temperUnit 2:temper
     */
    private ThermalPointInfoVo parsePointData(NetSDKLib.NET_RADIOMETRYINFO pointInfo) {
        ThermalPointInfoVo pointInfoVo = new ThermalPointInfoVo();
        String [] arrMeterType = Res.string().getMeterTypeList();
        if (pointInfo.nMeterType >= 1 &&
                pointInfo.nMeterType <= arrMeterType.length) {
            pointInfoVo.setMeterType(arrMeterType[pointInfo.nMeterType-1]);
        }else {
            pointInfoVo.setMeterType(Res.string().getShowInfo("UNKNOWN"));
        }
        String [] arrTemperUnit = Res.string().getTemperUnitList();
        if (pointInfo.nTemperUnit >= 1 &&
                pointInfo.nTemperUnit <= arrTemperUnit.length) {
            pointInfoVo.setTemperUnit(arrTemperUnit[pointInfo.nTemperUnit-1]);
        }else {
            pointInfoVo.setTemperUnit(Res.string().getShowInfo("UNKNOWN"));
        }
        pointInfoVo.setTemper(String.valueOf(pointInfo.fTemperAver));
        return pointInfoVo;
    }

    /**
     * 获取测温项信息
     * @param channel
     * @param presetId
     * @param ruleId
     * @param meterType
     * @return String[] 0:meterType 1:temperUnit 2:temperAver 3:temperMax 4:temperMin 5:temperMid 6:temperStd
     */
    public ThermalItemInfoVo queryItemInfo(int channel,int presetId,int ruleId,int meterType) {
        NetSDKLib.NET_RADIOMETRYINFO stItemInfo =
                ThermalCameraModule.queryItemTemper(channel, presetId, ruleId, meterType);
        if (stItemInfo == null) {
            log.error("获取测温项信息失败,code:{},message:{}", ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
            return null;
        }else{
            return parseItemData(stItemInfo);
        }
    }

    /**
     * 解析测温项信息
     * @param itemInfo
     * @return String[] 0:meterType 1:temperUnit 2:temperAver 3:temperMax 4:temperMin 5:temperMid 6:temperStd
     *
     */
    private ThermalItemInfoVo parseItemData(NetSDKLib.NET_RADIOMETRYINFO itemInfo) {
        ThermalItemInfoVo itemInfoVo = new ThermalItemInfoVo();
        String [] arrMeterType = Res.string().getMeterTypeList();
        if (itemInfo.nMeterType >= 1 &&
                itemInfo.nMeterType <= arrMeterType.length) {
            itemInfoVo.setMeterType(arrMeterType[itemInfo.nMeterType-1]);
        }else {
            itemInfoVo.setMeterType(Res.string().getShowInfo("UNKNOWN"));
        }
        String [] arrTemperUnit = Res.string().getTemperUnitList();
        if (itemInfo.nTemperUnit >= 1 &&
                itemInfo.nTemperUnit <= arrTemperUnit.length) {
            itemInfoVo.setTemperUnit(arrTemperUnit[itemInfo.nTemperUnit-1]);
        }else {
            itemInfoVo.setTemperUnit(Res.string().getShowInfo("UNKNOWN"));
        }
        itemInfoVo.setTemperAver(String.valueOf(itemInfo.fTemperAver));
        itemInfoVo.setTemperMax(String.valueOf(itemInfo.fTemperMax));
        itemInfoVo.setTemperMin(String.valueOf(itemInfo.fTemperMin));
        itemInfoVo.setTemperMid(String.valueOf(itemInfo.fTemperMid));
        itemInfoVo.setTemperStd(String.valueOf(itemInfo.fTemperStd));
        return itemInfoVo;
    }

    /**
     * 查询温度信息
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param meterType
     * @param channel
     * @param peridIndex
     * @return
     */
    public List<ThermalInfoVo> dofindTemper(int page,int size,
                                                      String startTime,String endTime,
                                                      int meterType,int channel,int peridIndex) {
        // 查询开始偏移量
        int offset = 0;
        if(page <= 0){
            return null;
        }else if(page == 1){
            ThermalCameraModule.stopFind();
            if (!ThermalCameraModule.startFind(getStuStartFind(startTime,endTime,meterType,channel,peridIndex))) {
                return null;
            }
        }else{
            offset = (page-1) * size + 1;
        }
        NetSDKLib.NET_OUT_RADIOMETRY_DOFIND stuDoFind = ThermalCameraModule.doFind(offset, size);
        return parseTemperDataToList(stuDoFind);
    }

    /**
     * 获取并构造查询参数
     * @param startTime
     * @param endTime
     * @param meterType
     * @param channel
     * @param peridIndex
     * @return
     */
    private NetSDKLib.NET_IN_RADIOMETRY_STARTFIND getStuStartFind(String startTime,String endTime,int meterType,int channel,int peridIndex) {
        NetSDKLib.NET_IN_RADIOMETRY_STARTFIND stuStartFind = new NetSDKLib.NET_IN_RADIOMETRY_STARTFIND();
        setTime(stuStartFind.stStartTime, startTime);
        setTime(stuStartFind.stEndTime, endTime);
        stuStartFind.nMeterType = meterType;
        stuStartFind.nChannel = channel;
        int[] arrPeriod = {5, 10, 15, 30};
        stuStartFind.emPeriod = arrPeriod[peridIndex];
        return stuStartFind;
    }

    /**
     * 设置时间参数
     * @param netTime
     * @param date yyyy-MM-dd HH:mm:ss
     */
    private void setTime(NetSDKLib.NET_TIME netTime, String date) {
        String[] dateTime = date.split(" ");
        String[] arrDate = dateTime[0].split("-");
        String[] arrTime = dateTime[1].split(":");
        netTime.dwYear = Integer.parseInt(arrDate[0]);
        netTime.dwMonth = Integer.parseInt(arrDate[1]);
        netTime.dwDay = Integer.parseInt(arrDate[2]);
        netTime.dwHour = Integer.parseInt(arrTime[0]);
        netTime.dwMinute = Integer.parseInt(arrTime[1]);
        netTime.dwSecond = Integer.parseInt(arrTime[2]);
    }

    /**
     * 解析温度数据为列表数据
     * @param stuDoFind
     * @return
     */
    private List<ThermalInfoVo> parseTemperDataToList(NetSDKLib.NET_OUT_RADIOMETRY_DOFIND stuDoFind) {
        if (stuDoFind == null) {
            return null;
        }
        List<ThermalInfoVo> data = new ArrayList<>(stuDoFind.nFound);
        for (int i = 0; i < stuDoFind.nFound; ++i) {
            data.add(parseTemperData(stuDoFind.stInfo[i]));
        }
        return data;
    }

    /**
     * 解析单个温度数据
     * @param data
     * @return
     */
    private ThermalInfoVo parseTemperData(NetSDKLib.NET_RADIOMETRY_QUERY data) {
        ThermalInfoVo thermalInfoVo = new ThermalInfoVo();
        thermalInfoVo.setTime(data.stTime.toStringTimeEx());
        thermalInfoVo.setPresetId(data.nPresetId);
        thermalInfoVo.setRuleId(data.nRuleId);
        try {
            thermalInfoVo.setName(new String(data.szName, "GBK").trim());
        } catch (UnsupportedEncodingException e) {
            thermalInfoVo.setName(new String(data.szName).trim());
        }
        thermalInfoVo.setChannel(data.nChannel);

        if (data.stTemperInfo.nMeterType == NetSDKLib.NET_RADIOMETRY_METERTYPE.NET_RADIOMETRY_METERTYPE_SPOT) {
            thermalInfoVo.setCoordinate("(" + data.stCoordinate.nx + "," + data.stCoordinate.ny + ")");
        }else {
            thermalInfoVo.setCoordinate(" ");
        }

        if (data.stTemperInfo.nMeterType >= 1 &&
                data.stTemperInfo.nMeterType <= arrMeterType.length) {
            thermalInfoVo.setMeterType(arrMeterType[data.stTemperInfo.nMeterType-1]);
        }else {
            thermalInfoVo.setMeterType(Res.string().getShowInfo("UNKNOWN"));
        }

        if (data.stTemperInfo.nTemperUnit >= 1 &&
                data.stTemperInfo.nTemperUnit <= arrTemperUnit.length) {
            thermalInfoVo.setTemperUnit(arrTemperUnit[data.stTemperInfo.nTemperUnit-1]);
        }else {
            thermalInfoVo.setTemperUnit(Res.string().getShowInfo("UNKNOWN"));
        }

        thermalInfoVo.setTemperAver(String.valueOf(data.stTemperInfo.fTemperAver));
        thermalInfoVo.setTemperMax(String.valueOf(data.stTemperInfo.fTemperMax));
        thermalInfoVo.setTemperMin(String.valueOf(data.stTemperInfo.fTemperMin));
        thermalInfoVo.setTemperMid(String.valueOf(data.stTemperInfo.fTemperMid));
        thermalInfoVo.setTemperStd(String.valueOf(data.stTemperInfo.fTemperStd));

        return thermalInfoVo;
    }

    /**
     * 订阅温度分布数据（热图）
     * @param channel
     */
    public void attach(int channel,RadiometryAttachCB radiometryAttachCB) {
        freeMemory();
        if (ThermalCameraModule.isAttaching()) {
            // 取消订阅温度分布数据
            ThermalCameraModule.radiometryDetach();
        }else {
            // 订阅温度分布数据,回调中处理数据
            if (ThermalCameraModule.radiometryAttach(channel, radiometryAttachCB)) {
                // 清除数据并设置可以fetch
            }else {
                log.error("attach失败,code:{},message:{}",ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
            }
        }
    }

    /**
     * 开始获取热图数据
     * @param channel
     * @return
     */
    public boolean fetch(int channel) {
        freeMemory();
        // 设置不可以save
        // 开始获取热图数据
        int nStatus = ThermalCameraModule.radiometryFetch(channel);
        return nStatus != -1;
    }

    /**
     * 处理回调数据（热图）
     * @return
     */
    public boolean save() {
        boolean bSaved = false;
        try {
            lock.lock();
            bSaved = ThermalCameraModule.saveData(gData);
        } finally {
            lock.unlock();
        }
        return bSaved;
    }

    /**
     * 释放内存
     */
    private void freeMemory() {
        try {
            lock.lock();
            if (gData.pbDataBuf != null) {
                Native.free(Pointer.nativeValue(gData.pbDataBuf));
                Pointer.nativeValue(gData.pbDataBuf, 0);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 复制获取的热图数据
     * @param data
     */
    private void copyRadiometryData(NetSDKLib.NET_RADIOMETRY_DATA data) {
        try {
            lock.lock();
            gData.stMetaData = data.stMetaData;
            gData.dwBufSize = data.dwBufSize;
            gData.pbDataBuf = new Memory(data.dwBufSize);
            gData.pbDataBuf.write(0, data.pbDataBuf.getByteArray(0, data.dwBufSize), 0, data.dwBufSize);
        } finally {
            lock.unlock();
        }
    }

    /**
     *  解析获取的热图数据
     * @return String[] 0:height 1:width 2:channel 3:time 4:length 5:sensorType
     */
    private String[] parseHeatMapData() {
        String[] data = new String[6];
        try {
            lock.lock();
            data[0] = String.valueOf(gData.stMetaData.nHeight);
            data[1] = String.valueOf(gData.stMetaData.nWidth);
            data[2] = String.valueOf(gData.stMetaData.nChannel+1);
            data[3] = gData.stMetaData.stTime.toStringTimeEx();
            data[4] = String.valueOf(gData.stMetaData.nLength);
            try {
                data[5] = new String(gData.stMetaData.szSensorType, "GBK").trim();
            } catch (UnsupportedEncodingException e) {
                data[5] = new String(gData.stMetaData.szSensorType).trim();
            }
        } finally {
            lock.unlock();
        }
        return data;
    }

    /**
     * 设备断线回调
     * 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
     */
    private static class DisConnectCallback implements NetSDKLib.fDisConnect {
        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            log.error("Device[{}] Port[{}] DisConnect!", pchDVRIP, nDVRPort);
        }
    }

    /**
     * 网络连接恢复，设备重连成功回调
     * 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
     */
    private static class HaveReConnectCallback implements NetSDKLib.fHaveReConnect {
        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            log.error("ReConnect Device[{}] Port[{}]", pchDVRIP, nDVRPort);
        }
    }

    /**
     * 订阅回调
     */
    private class RadiometryAttachCB implements NetSDKLib.fRadiometryAttachCB {
        @Override
        public void invoke(NetSDKLib.LLong lAttachHandle, final NetSDKLib.NET_RADIOMETRY_DATA pBuf,
                           int nBufLen, Pointer dwUser) {
            copyRadiometryData(pBuf);
            // 完成后设置可以保存并更新数据
        }
    }
}