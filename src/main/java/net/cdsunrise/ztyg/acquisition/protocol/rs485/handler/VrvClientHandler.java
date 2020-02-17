package net.cdsunrise.ztyg.acquisition.protocol.rs485.handler;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class VrvClientHandler extends ChannelInboundHandlerAdapter {
    @Getter
    private ChannelHandlerContext ctx;
    private ChannelPromise promise;
    private String data;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        if(buf != null){
            try {
                // 解析设备数据
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                if(bytes != null){
                    if(promise != null){
                        promise.setSuccess();
                        data = HexUtil.encodeHexStr(bytes).toUpperCase();
                        log.info("<<<<VrvClient收到设备数据:{}",data);
                    }
                }
            } finally {
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

    public String getData(){
        promise = null;
        return data;
    }
}
