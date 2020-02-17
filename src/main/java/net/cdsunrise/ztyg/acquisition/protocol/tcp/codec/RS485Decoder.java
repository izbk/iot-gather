package net.cdsunrise.ztyg.acquisition.protocol.tcp.codec;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485RegisterMsg;

/**
 * @author: LiuWei
 * @date: Create in 16:06 2018/11/28
 */
@Slf4j
public class RS485Decoder {
    private static final int ILS_PROTOCOL_LENGTH = 8;
    @SuppressWarnings("Duplicates")
    public RS485Msg bytesToRS485Msg(ByteBuf data) {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        if(bytes.length != ILS_PROTOCOL_LENGTH){
            return null;
        }
        byte[] b = new byte[1];
        // 地址
        b[0] = bytes[0];
        String address = HexUtil.encodeHexStr(b).toUpperCase();
        // 指令代码
        b[0] = bytes[1];
        String code = HexUtil.encodeHexStr(b).toUpperCase();
        // 楼层代码
        b[0] = bytes[2];
        String floorCode = HexUtil.encodeHexStr(b).toUpperCase();
        // 房间代码
        b[0] = bytes[3];
        String roomCode = HexUtil.encodeHexStr(b).toUpperCase();
        // 设备代码
        b[0] = bytes[4];
        String deviceSn = HexUtil.encodeHexStr(b).toUpperCase();
        // 数据位
        b[0] = bytes[5];
        String d6 = HexUtil.encodeHexStr(b).toUpperCase();
        // 数据位
        b[0] = bytes[6];
        String d7 = HexUtil.encodeHexStr(b).toUpperCase();
        // 检验位
        b[0] = bytes[7];
        String verifyCode = HexUtil.encodeHexStr(b).toUpperCase();
        return new RS485Msg(address,code,floorCode,roomCode,deviceSn,d6,d7,verifyCode);
    }
    /**
     * 解析注册信息
     *
     * @param data
     */
    @SuppressWarnings("Duplicates")
    public RS485RegisterMsg bytesToRegisterMsg(byte[] data) {
        //协议数据D1~D8共8个字节 D3~D5分别是楼层、房间、设备编号
        byte[] b = new byte[1];
        b[0] = data[2];
        String floorCode  = HexUtil.encodeHexStr(b).toUpperCase();
        b[0] = data[3];
        String roomCode  = HexUtil.encodeHexStr(b).toUpperCase();
        b[0] = data[4];
        String deviceCode  = HexUtil.encodeHexStr(b).toUpperCase();
        return new RS485RegisterMsg(floorCode,roomCode,deviceCode);
    }
}
