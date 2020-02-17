package net.cdsunrise.ztyg.acquisition.dahua.service;

import lombok.extern.slf4j.Slf4j;
import main.java.com.netsdk.common.Res;
import main.java.com.netsdk.lib.NetSDKLib;
import main.java.com.netsdk.lib.ToolKits;
import net.cdsunrise.ztyg.acquisition.dahua.module.LoginModule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录服务
 * @author Binke Zhang
 * @date 2020/2/17 9:26
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class LoginService {

    /**
     * 打开工程，初始化
     * @return
     */
    public boolean init(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect haveReConnect){
        return LoginModule.init(disConnect, haveReConnect);
    }

    /**
     * 登录
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return boolean
     */
    public boolean login(String ip, int port, String username, String password) {
        if(LoginModule.login(ip, port,username,password)) {
            return true;
        } else {
            log.error("登录失败:{},code:{},message:{}",Res.string().getLoginFailed(), ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
            return false;
        }
    }

    /**
     * 登录并获取通道
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return List<String>
     */
    public List<String> loginAndGetChannel(String ip, int port, String username, String password) {
        List<String> channelList = new ArrayList<>();
        if(LoginModule.login(ip, port,username,password)) {
            for(int i = 1; i < LoginModule.m_stDeviceInfo.byChanNum + 1; i++) {
                channelList.add(Res.string().getChannel() + " " + i);
            }
        } else {
            log.error("登录失败:{},code:{},message:{}",Res.string().getLoginFailed(),ToolKits.getErrorCodeShow(), Res.string().getErrorMessage());
        }
        return channelList;
    }

    /**
     * 登出
     */
    public void logout() {
        LoginModule.logout();
    }
}
