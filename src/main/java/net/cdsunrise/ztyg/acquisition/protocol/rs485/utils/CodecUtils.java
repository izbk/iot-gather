package net.cdsunrise.ztyg.acquisition.protocol.rs485.utils;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.constant.ScaleSystem;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.msg.RS485Msg;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Binke Zhang
 * @date 2019/11/28 9:30
 */
public class CodecUtils {
    /**
     * 转换发送消息为int数组
     * @param msg
     * @return
     */
    public static int[] convertMsgToArray(RS485Msg msg){
        // 地址
        Integer address = Integer.parseInt(msg.getAddress(), ScaleSystem.HEX);
        // 指令代码
        Integer code = Integer.parseInt(msg.getCode(), ScaleSystem.HEX);
        // 楼层代码
        Integer floorCode = Integer.parseInt(msg.getFloorCode(), ScaleSystem.HEX);
        // 房间代码
        Integer roomCode = Integer.parseInt(msg.getRoomCode(), ScaleSystem.HEX);
        // 设备代码
        Integer deviceSn = Integer.parseInt(msg.getDeviceSn(), ScaleSystem.HEX);
        // 数据位
        Integer d6 = Integer.parseInt(msg.getD6(), ScaleSystem.HEX);
        // 数据位
        Integer d7 = Integer.parseInt(msg.getD7(), ScaleSystem.HEX);
        // 校验位
        Integer ch = 0;
        if(StringUtils.isNotEmpty(msg.getVerifyCode())){
            ch = Integer.parseInt(msg.getVerifyCode(), ScaleSystem.HEX);
        }
        return new int[]{address,code,floorCode,roomCode,deviceSn,d6,d7,ch};
    }

    /**
     * 计算校验位
     * @param b
     * @return
     */
    public static int getCHVerify(int[] b) {
        int tempCrc = 0;
        int multiplier = 8;
        for (int i = 0; i < 7; i++) {
            tempCrc += b[i] * multiplier--;
        }
        tempCrc = tempCrc % 256;
        return tempCrc;
    }

    /**
     * 转换消息int[]为ByteBuf
     * @param msg
     * @return
     */
    public static ByteBuf getByteBuf(RS485Msg msg) {
        ByteBuf heapBuf = Unpooled.buffer(8);
        int[] data = CodecUtils.convertMsgToArray(msg);
        for (int i = 0; i < data.length; i++) {
            heapBuf.writeByte(data[i]);
        }
        return heapBuf;
    }

    /**
     * 转换消息int[]为ByteBuf
     * @param msg
     * @return
     */
    public static ByteBuf getByteBuf(String msg) {
        return Unpooled.copiedBuffer(HexUtil.decodeHex(msg));
    }
}
