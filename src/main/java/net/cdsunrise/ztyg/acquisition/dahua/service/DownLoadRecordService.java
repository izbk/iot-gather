package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.common.SavePath;
import main.java.com.netsdk.lib.NetSDKLib;
import net.cdsunrise.ztyg.acquisition.dahua.module.DownLoadRecordModule;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载录像服务
 * 主要有 ： 查询录像、下载录像、设置码流类型功能
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class DownLoadRecordService {
    /**
     * 查找录像文件
     * @param channelId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<String[]> queryRecordFile(int channelId,
                                          String startTime,
                                          String endTime){
        NetSDKLib.NET_TIME stTimeStart = new NetSDKLib.NET_TIME();
        NetSDKLib.NET_TIME stTimeEnd = new NetSDKLib.NET_TIME();
        // 开始时间
        String[] dateStartByFile = startTime.split(" ");
        String[] dateStart1 = dateStartByFile[0].split("-");
        String[] dateStart2 = dateStartByFile[1].split(":");

        stTimeStart.dwYear = Integer.parseInt(dateStart1[0]);
        stTimeStart.dwMonth = Integer.parseInt(dateStart1[1]);
        stTimeStart.dwDay = Integer.parseInt(dateStart1[2]);
        stTimeStart.dwHour = Integer.parseInt(dateStart2[0]);
        stTimeStart.dwMinute = Integer.parseInt(dateStart2[1]);
        stTimeStart.dwSecond = Integer.parseInt(dateStart2[2]);
        // 结束时间
        String[] dateEndByFile = endTime.split(" ");
        String[] dateEnd1 = dateEndByFile[0].split("-");
        String[] dateEnd2 = dateEndByFile[1].split(":");

        stTimeEnd.dwYear = Integer.parseInt(dateEnd1[0]);
        stTimeEnd.dwMonth = Integer.parseInt(dateEnd1[1]);
        stTimeEnd.dwDay = Integer.parseInt(dateEnd1[2]);
        stTimeEnd.dwHour = Integer.parseInt(dateEnd2[0]);
        stTimeEnd.dwMinute = Integer.parseInt(dateEnd2[1]);
        stTimeEnd.dwSecond = Integer.parseInt(dateEnd2[2]);

        if(stTimeStart.dwYear != stTimeEnd.dwYear
                || stTimeStart.dwMonth != stTimeEnd.dwMonth
                || (stTimeEnd.dwDay - stTimeStart.dwDay > 1)) {
            log.error("时间参数校验错误:{},code:{},message:{}", Res.string().getSelectTimeAgain(), Res.string().getErrorMessage(), JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int time = 0;
        if(stTimeEnd.dwDay - stTimeStart.dwDay == 1) {
            time = (24 + stTimeEnd.dwHour)*60*60 + stTimeEnd.dwMinute*60 + stTimeEnd.dwSecond -
                    stTimeStart.dwHour*60*60 - stTimeStart.dwMinute*60 - stTimeStart.dwSecond;
        } else {
            time = stTimeEnd.dwHour*60*60 + stTimeEnd.dwMinute*60 + stTimeEnd.dwSecond -
                    stTimeStart.dwHour*60*60 - stTimeStart.dwMinute*60 - stTimeStart.dwSecond;
        }

        if(time > 6 * 60 * 60
                || time <= 0) {
            log.error("时间参数校验错误:{},code{},message:{}", Res.string().getSelectTimeAgain(), Res.string().getErrorMessage(), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        IntByReference nFindCount = new IntByReference(0);
        // 录像文件信息
        NetSDKLib.NET_RECORDFILE_INFO[] stFileInfo = (NetSDKLib.NET_RECORDFILE_INFO[])new NetSDKLib.NET_RECORDFILE_INFO().toArray(2000);
        List<String[]> data = null;
        boolean haveRecordFile = DownLoadRecordModule.queryRecordFile(channelId, stTimeStart, stTimeEnd, stFileInfo, nFindCount);
        if(haveRecordFile){
            int count = nFindCount.getValue();
            if(count == 0) {
                return null;
            }else{
                data = new ArrayList<>(count);
                for(int j = 0; j < nFindCount.getValue(); j++) {
                    String[] arr = new String[4];
                    // 设备返回的通道加1
                    arr[0] = String.valueOf(stFileInfo[j].ch + 1);
                    // RecordFileType 录像类型 0:所有录像  1:外部报警  2:动态监测报警  3:所有报警  4:卡号查询   5:组合条件查询
                    // 6:录像位置与偏移量长度   8:按卡号查询图片(目前仅HB-U和NVS特殊型号的设备支持)  9:查询图片(目前仅HB-U和NVS特殊型号的设备支持)
                    // 10:按字段查询    15:返回网络数据结构(金桥网吧)  16:查询所有透明串数据录像文件
                    arr[1] = Res.string().getRecordTypeStr(stFileInfo[j].nRecordFileType);
                    arr[2] = stFileInfo[j].starttime.toStringTime();
                    arr[3] = stFileInfo[j].endtime.toStringTime();
                    data.add(arr);
                }
            }
        }
        return data;
    }

    /**
     *  设置回放时的码流类型
     * @param m_streamType 码流类型
     */
    public static void setStreamType(int streamType){
        DownLoadRecordModule.setStreamType(streamType);
    }

    /**
     *  下载录像文件
     * @param nChannelId
     * @param nRecordFileType
     * @param startTime
     * @param endTime
     * @param SavedFileName
     * @param cbTimeDownLoadPos
     * @return
     */
    public NetSDKLib.LLong downloadRecordFile(int nChannelId,
                                                     int nRecordFileType,
                                                     String startTime,
                                                     String endTime,
                                                     String SavedFileName,
                                                     NetSDKLib.fTimeDownLoadPosCallBack cbTimeDownLoadPos){

        NetSDKLib.NET_TIME stTimeStart = new NetSDKLib.NET_TIME();
        NetSDKLib.NET_TIME stTimeEnd = new NetSDKLib.NET_TIME();
        // 开始时间
        String[] dateStart = startTime.split(" ");
        String[] dateStartByFile1 = dateStart[0].split("/");
        String[] dateStartByFile2 = dateStart[1].split(":");

        stTimeStart.dwYear = Integer.parseInt(dateStartByFile1[0]);
        stTimeStart.dwMonth = Integer.parseInt(dateStartByFile1[1]);
        stTimeStart.dwDay = Integer.parseInt(dateStartByFile1[2]);

        stTimeStart.dwHour = Integer.parseInt(dateStartByFile2[0]);
        stTimeStart.dwMinute = Integer.parseInt(dateStartByFile2[1]);
        stTimeStart.dwSecond = Integer.parseInt(dateStartByFile2[2]);

        // 结束时间
        String[] dateEnd = endTime.split(" ");
        String[] dateEndByFile1 = dateEnd[0].split("/");
        String[] dateEndByFile2 = dateEnd[1].split(":");

        stTimeEnd.dwYear = Integer.parseInt(dateEndByFile1[0]);
        stTimeEnd.dwMonth = Integer.parseInt(dateEndByFile1[1]);
        stTimeEnd.dwDay = Integer.parseInt(dateEndByFile1[2]);

        stTimeEnd.dwHour = Integer.parseInt(dateEndByFile2[0]);
        stTimeEnd.dwMinute = Integer.parseInt(dateEndByFile2[1]);
        stTimeEnd.dwSecond = Integer.parseInt(dateEndByFile2[2]);
        return DownLoadRecordModule.downloadRecordFile(nChannelId,
                nRecordFileType,
                stTimeStart,
                stTimeEnd,
                SavePath.getSavePath().getSaveRecordFilePath(),
                null);
    }

    /**
     * 停止下载
     * @param downLoadHandle
     */
    public void stopDownLoadRecordFile(NetSDKLib.LLong downLoadHandle){
        DownLoadRecordModule.stopDownLoadRecordFile(downLoadHandle);
    }

}
