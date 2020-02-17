package net.cdsunrise.ztyg.acquisition.protocol.tcp.msg;


import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据上报消息
 *
 * @author: LiuWei
 * @date: Create in 23:56 2018/12/14
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RS485Msg {

    /**
     * 地址
     */
    private String address;
    /**
     * 指令代码
     */
    private String code;
    /**
     * 楼层代码
     */
    private String floorCode;
    /**
     * 房间代码
     */
    private String roomCode;
    /**
     * 设备代码
     */
    private String deviceSn;
    /**
     * 数据位
     */
    private String d6;
    /**
     * 数据位
     */
    private String d7;
    /**
     * 检验位
     */
    private String verifyCode;

    /**
     * 获取设备标识符
     * @return
     */
    public String getDeviceIdentifier(){
        return StrUtil.concat(false,address,floorCode,roomCode,deviceSn);
    }

    @Override
    public String toString(){
        return StrUtil.concat(false,address,code,floorCode,roomCode,deviceSn,d6,d7,verifyCode);
    }
}
