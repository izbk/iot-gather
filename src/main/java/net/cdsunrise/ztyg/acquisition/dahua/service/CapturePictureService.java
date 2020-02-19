package net.cdsunrise.ztyg.acquisition.dahua.service;

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.common.SavePath;
import main.java.com.netsdk.demo.module.LoginModule;
import main.java.com.netsdk.demo.module.RealPlayModule;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.dahua.module.CapturePictureModule;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 抓图服务
 * 包含: 本地、远程、定时和停止抓图
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class CapturePictureService {
    private CaptureReceiveCB m_CaptureReceiveCB = new CaptureReceiveCB();
    // This field indicates whether the device is playing
    private boolean bRealPlay = false;
    // This field indicates whether the device is timing capture
    private boolean bTimerCapture = false;
    // realplay handle
    public static NetSDKLib.LLong m_hPlayHandle = new NetSDKLib.LLong(0);

    /**
     *  登录并设置远程抓图、定时抓图的回调函数
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return list 通道列表
     */
    public List<String> loginAndSetCallBack(String ip, int port, String username, String password) {
        // 设置远程抓图、定时抓图的回调函数
        Native.setCallbackThreadInitializer(m_CaptureReceiveCB,
                new CallbackThreadInitializer(false, false, "snapPicture callback thread"));
        List<String> channelList = new ArrayList<>();
        if(LoginModule.login(ip, port, username, password)) {
            for(int i = 1; i < LoginModule.m_stDeviceInfo.byChanNum + 1; i++) {
                channelList.add(Res.string().getChannel() + " " + String.valueOf(i));
            }
            CapturePictureModule.setSnapRevCallBack(m_CaptureReceiveCB);
        } else {
            log.error("登录失败:{},code:{},message:{}",Res.string().getLoginFailed(),ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
        }
        return channelList;
    }

    public void logout(int channel) {
        if (bTimerCapture) {
            CapturePictureModule.stopCapturePicture(channel);
        }
        RealPlayModule.stopRealPlay(m_hPlayHandle);
        LoginModule.logout();
        bRealPlay = false;
        bTimerCapture = false;
    }

    /**
     * 本地抓图
     * @param hPlayHandle
     * @param picFileName
     * @return
     */
    public String localCapturePicture(){
        String strFileName = SavePath.getSavePath().getSaveCapturePath();
        System.out.println("strFileName = " + strFileName);
        CapturePictureModule.localCapturePicture(m_hPlayHandle, strFileName);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(strFileName));
            if(bufferedImage == null) {
                return null;
            }
            ImageIO.write(bufferedImage, "jpg", new File(strFileName));
        } catch (IOException e) {
            log.error("本地抓图失败:{}",e.getMessage());
        }
        return strFileName;
    }


    /**
     * 远程抓图
     * @param chn
     * @return
     */
    public boolean remoteCapturePicture(int chn){
       return CapturePictureModule.remoteCapturePicture(chn);
    }

    /**
     * 定时抓图
     * @param chn
     * @return
     */
    public boolean timerCapturePicture(int chn){
        return CapturePictureModule.timerCapturePicture(chn);
    }

    /**
     * 停止定时抓图
     * @param chn
     * @return
     */
    public boolean stopCapturePicture(int chn){
        return CapturePictureModule.stopCapturePicture(chn);
    }

    /**
     * 抓图回调函数原形(pBuf内存由SDK内部申请释放)
     */
    private class CaptureReceiveCB implements NetSDKLib.fSnapRev{
        BufferedImage bufferedImage = null;
        /**
         * 回调处理
         * @param lLoginID
         * @param pBuf
         * @param RevLen
         * @param EncodeType 编码类型，10：表示jpeg图片      0：mpeg4
         * @param CmdSerial 操作流水号
         * @param dwUser
         */
        @Override
        public void invoke(NetSDKLib.LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser) {
            if(pBuf != null && RevLen > 0) {
                String strFileName = SavePath.getSavePath().getSaveCapturePath();
                System.out.println("strFileName = " + strFileName);
                byte[] buf = pBuf.getByteArray(0, RevLen);
                ByteArrayInputStream byteArrInput = new ByteArrayInputStream(buf);
                try {
                    bufferedImage = ImageIO.read(byteArrInput);
                    if(bufferedImage == null) {
                        return;
                    }
                    ImageIO.write(bufferedImage, "jpg", new File(strFileName));
                } catch (IOException e) {
                    log.error("抓图失败:{}",e.getMessage());
                }
                // show picture	 TODO
            }
        }
    }
}
