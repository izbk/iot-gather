package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import net.cdsunrise.ztyg.acquisition.dahua.module.AlarmListenModule;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * 报警服务
 * 包含 ：向设备订阅报警和停止订阅报警
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class AlarmListenService {
    /**
     * 订阅事件回调
     */
    private AlarmDataCB alarmDataCB = new AlarmDataCB();
    /**
     * 事件数据列表
     */
    Vector<AlarmEventInfo> data = new Vector<AlarmEventInfo>();

    /**
     * 向设备订阅报警
     *
     * @param cbMessage
     * @return
     */
    public boolean startListen() {
        return AlarmListenModule.startListen(alarmDataCB);
    }

    /**
     * 停止订阅报警
     *
     * @return
     */
    public boolean stopListen() {
        return AlarmListenModule.stopListen();
    }

    /**
     * 事件回调获取数据
     */
     private class AlarmDataCB implements NetSDKLib.fMessCallBack {
         private final EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

         @Override
         public boolean invoke(int lCommand, NetSDKLib.LLong lLoginID,
                               Pointer pStuEvent, int dwBufLen, String strDeviceIP,
                               NativeLong nDevicePort, Pointer dwUser) {
             switch (lCommand) {
                 case NetSDKLib.NET_ALARM_ALARM_EX:
                 case NetSDKLib.NET_MOTION_ALARM_EX:
                 case NetSDKLib.NET_VIDEOLOST_ALARM_EX:
                 case NetSDKLib.NET_SHELTER_ALARM_EX:
                 case NetSDKLib.NET_DISKFULL_ALARM_EX:
                 case NetSDKLib.NET_DISKERROR_ALARM_EX: {
                     byte[] alarm = new byte[dwBufLen];
                     pStuEvent.read(0, alarm, 0, dwBufLen);
                     for (int i = 0; i < dwBufLen; i++) {
                         if (alarm[i] == 1) {
                             AlarmEventInfo alarmEventInfo = new AlarmEventInfo(i, lCommand, AlarmStatus.ALARM_START);
                             if (!data.contains(alarmEventInfo)) {
                                 // 添加数据
                                 data.add(alarmEventInfo);
                             }
                         } else {
                             AlarmEventInfo alarmEventInfo = new AlarmEventInfo(i, lCommand, AlarmStatus.ALARM_STOP);
                             // 删除数据
                             data.remove(alarmEventInfo);
                         }
                     }
                     break;
                 }
                 default:
                     break;
             }
             return true;
         }
     }

    /**
     * 转换事件数据
     * @param alarmEventInfo
     * @return String[] 0:index 1:eventTime 2:channel 3:message
     */
    public String[] convertAlarmEventInfo(AlarmEventInfo alarmEventInfo) {
        String[] data = new String[4];
        data[0] = String.valueOf(alarmEventInfo.id);
        data[1] = formatDate(alarmEventInfo.date);
        data[2] = String.valueOf(alarmEventInfo.chn);
        String status = null;
        if (alarmEventInfo.status == AlarmStatus.ALARM_START) {
            status = Res.string().getStart();
        }else {
            status = Res.string().getStop();
        }
        data[3] = alarmMessageMap.get(alarmEventInfo.type) + status;

        return data;
    }

    private String formatDate(Date date) {
        final SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDate.format(date);
    }

    /**
     * 事件消息Map
     */
    private static HashMap<Integer, String> alarmMessageMap = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;
        {
            put(NetSDKLib.NET_ALARM_ALARM_EX, Res.string().getExternalAlarm());
            put(NetSDKLib.NET_MOTION_ALARM_EX, Res.string().getMotionAlarm());
            put(NetSDKLib.NET_VIDEOLOST_ALARM_EX, Res.string().getVideoLostAlarm());
            put(NetSDKLib.NET_SHELTER_ALARM_EX, Res.string().getShelterAlarm());
            put(NetSDKLib.NET_DISKFULL_ALARM_EX, Res.string().getDiskFullAlarm());
            put(NetSDKLib.NET_DISKERROR_ALARM_EX, Res.string().getDiskErrorAlarm());
        }
    };

    /**
     * 事件状态
     */
    enum AlarmStatus {
        ALARM_START, ALARM_STOP
    }

    /**
     * 事件数据
     */
    static class AlarmEventInfo {
        public static long index = 0;
        public long id;
        public int chn;
        public int type;
        public Date date;
        public AlarmStatus status;

        public AlarmEventInfo(int chn, int type, AlarmStatus status) {
            this.chn = chn;
            this.type = type;
            this.status = status;
            this.date = new Date();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AlarmEventInfo showInfo = (AlarmEventInfo) o;
            return chn == showInfo.chn && type == showInfo.type;
        }
    }

}

    

