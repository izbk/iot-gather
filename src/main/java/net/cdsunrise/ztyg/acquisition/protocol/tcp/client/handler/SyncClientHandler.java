package net.cdsunrise.ztyg.acquisition.protocol.tcp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.ils.service.DataProcessService;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.codec.RS485Decoder;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 同步获取服务端处理结果
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class SyncClientHandler extends ChannelInboundHandlerAdapter {
    private RS485Decoder rs485Decoder = new RS485Decoder();
    private static final String ENV_HEX_CODE = "C6";
    private static final String CONTROL_HEX_CODE = "C7";

    @Getter
    private ChannelHandlerContext ctx;
    private ChannelPromise promise;
    @Getter
    private RS485Msg data;

    @Autowired
    private DataProcessService dataProcessService;
    @Override
    @SuppressWarnings("Duplicates")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if(buf != null){
            try {
                // 解析设备数据
                RS485Msg rs485Msg = rs485Decoder.bytesToRS485Msg(buf);
                if(rs485Msg != null){
                    String code = rs485Msg.getCode();
                    // 客户端发送指令的返回消息
                    // 控制指令结果返回
                    if(CONTROL_HEX_CODE.equals(code)){
                        dataProcessService.updateDeviceState(rs485Msg);
                        if(promise != null){
                            log.info("<<<<RS485Client收到设备数据:{}",rs485Msg.toString());
                            promise.setSuccess();
                            data = rs485Msg;
                        }
                    }
                    else{
                        // 服务端主动推送的环境参数消息
                        if(ENV_HEX_CODE.equals(code)){
                            dataProcessService.processEnvironmentMsg(rs485Msg);
                        }
                    }
                }
            }finally {
                ReferenceCountUtil.release(buf);
            }
        }
    }

    /****
     * 当socket连接的时候会调用此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----连接" + ctx.channel().remoteAddress().toString());
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    /***
     * 客户端断开连接的时候会执行此方法
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----断开" + ctx.channel().remoteAddress().toString());
        this.ctx = null;
        super.channelInactive(ctx);
    }

    /***
     * 当客户端与服务器有异常时抛出错误
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
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

    public ChannelPromise sendMessage(Object message) {
        if (ctx == null){
            throw new IllegalStateException();
        }
        promise = ctx.writeAndFlush(message).channel().newPromise();
        return promise;
    }

    public RS485Msg getData(){
        promise = null;
        return data;
    }
}
