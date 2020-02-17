package net.cdsunrise.ztyg.acquisition.protocol.rs485.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.codec.RS485Decoder;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.constant.CacheKey;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.msg.RS485Msg;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.utils.CodecUtils;
import org.springframework.stereotype.Component;


/**
 * 消息处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RS485ServerHandler extends ChannelInboundHandlerAdapter {
    private RS485Decoder rs485Decoder = new RS485Decoder();
    private int lossConnectCount = 0;
    private final CommonCache<String, Channel> channelCommonCache = CommonCache.getInstance();
    private final CommonCache<String, RS485Msg> responseCache = CommonCache.getInstance();

    @Override
    @SuppressWarnings("Duplicates")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf buf = (ByteBuf) msg;
            /*if (buf.readableBytes() <= 0) {
                return;
            }
            //地址
            byte addr = buf.readByte();
            if (addr == -2 || addr == 116) {
                lossConnectCount = 0;
                log.info("心跳包...------------------------地址: {}", addr);
                return;
            }
            buf.resetReaderIndex();

            // 解析设备数据
            log.info(">>>>>>>>>>>>>>>>> Server收到设备数据新消息");
            RS485Msg rs485Msg = rs485Decoder.bytesToRS485Msg(buf);
            if(rs485Msg != null){
                // TODO 消息处理 这里模拟设备数据返回
                ByteBuf heapBuf = CodecUtils.getByteBuf(rs485Msg);*/
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
                channelCommonCache.get(CacheKey.SERVER_CHANNEL).writeAndFlush(buf);
//            }
        } catch (Exception e) {
            log.error(">>>>>>>[解析出错]:{}", e.getMessage(), e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        log.error("发生异常:{}", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive device is ready to connect");
        channelCommonCache.put(CacheKey.SERVER_CHANNEL,ctx.channel());
        log.info("终端连接:{}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)throws Exception {
        channelCommonCache.remove(CacheKey.SERVER_CHANNEL);
        log.info("终端断开连接:{}", ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                lossConnectCount ++;
                if(lossConnectCount > 2){
                    log.debug("心跳超时服务器主动断开:{}", ctx.channel());
                    ctx.close();
                }
            }
        }
    }

    private void release(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}