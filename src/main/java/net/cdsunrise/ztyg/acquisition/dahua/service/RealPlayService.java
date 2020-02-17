package net.cdsunrise.ztyg.acquisition.dahua.service;

import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.lib.NetSDKLib;
import net.cdsunrise.ztyg.acquisition.dahua.module.RealPlayModule;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * 实时预览接口实现
 * 主要有 ：开始拉流、停止拉流功能
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RealPlayService {
    /**
     * API 中有jwt类型的参数，暂不处理
     */

    /**
     * 开始预览
     * @param channel
     * @param stream
     * @param realPlayWindow
     * @return
     */
    public NetSDKLib.LLong startRealPlay(int channel, int stream, Panel realPlayWindow){
        return RealPlayModule.startRealPlay(channel, stream, realPlayWindow);
    }

    public void stopRealPlay(NetSDKLib.LLong m_hPlayHandle){
        RealPlayModule.stopRealPlay(m_hPlayHandle);
    }
}
