package net.cdsunrise.ztyg.acquisition.protocol.rs485;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.codec.RS485Decoder;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.constant.ScaleSystem;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.msg.RS485Msg;

/**
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        RS485Decoder decoder = new RS485Decoder();
        RS485Msg rs485Msg = decoder.bytesToRS485Msg(buf);
        log.info("服务端消息：{}",rs485Msg.toString());
        buf.resetReaderIndex();
        ctx.writeAndFlush(msg);
    }

    /***
     * 客户端断开连接的时候会执行此方法
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----断开" + ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    /***
     * 当客户端与服务器有异常时抛出错误
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    /****
     * 当socket连接的时候会调用此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----连接" + ctx.channel().remoteAddress().toString());
        ctx.writeAndFlush("xyz");
        ctx.channel().writeAndFlush("xyz");
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
//            IdleStateEvent event = (IdleStateEvent)evt;
//            if (event.state()== IdleState.READER_IDLE){
//                    System.out.println("客户端循环心跳监测发送: "+new Date());
//                    ctx.writeAndFlush(116);
//                }
        }
    }

    /**
     * 转换消息为int数组
     * @param msg
     * @return
     */
    private static int[] convertMsgToArray(RS485Msg msg){
        // 地址
        Integer address = Integer.parseInt(msg.getAddress(), ScaleSystem.HEX);
        // 指令代码
        Integer code = Integer.parseInt(msg.getCode(), ScaleSystem.HEX);
        // 楼层代码
        Integer floorCode = Integer.parseInt(msg.getFloorCode(), ScaleSystem.HEX);
        // 房间代码
        Integer roomCode = Integer.parseInt(msg.getRoomCode(), ScaleSystem.HEX);
        // 设备代码
        Integer deviceCode = Integer.parseInt(msg.getDeviceSn(), ScaleSystem.HEX);
        // 数据位
        Integer d6 = Integer.parseInt(msg.getD6(), ScaleSystem.HEX);
        // 数据位
        Integer d7 = Integer.parseInt(msg.getD7(), ScaleSystem.HEX);
        int[] data = new int[]{address,code,floorCode,roomCode,deviceCode,d6,d7};
        return data;
    }
    private static int getCHVerify(int[] b) {
        int tempCrc = 0;
        int multiplier = 8;
        for (int i = 0; i < b.length; i++) {
            tempCrc += b[i] * multiplier--;
//            System.out.println("b[i]="+b[i]+"temp="+tempCrc);
        }
//        System.out.println(b[0]*8+b[1]*7+b[2]*6+b[3]*5+b[4]*4+b[5]*3+b[6]*2);
        tempCrc = tempCrc % 256;
//        System.out.println("CH = [" + HexUtil.toUnicodeHex(tempCrc) + "]");
        return tempCrc;
    }
}
