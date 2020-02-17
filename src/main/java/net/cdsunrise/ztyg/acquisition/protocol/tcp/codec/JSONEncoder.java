package net.cdsunrise.ztyg.acquisition.protocol.tcp.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * JSON编码器
 * @author Binke Zhang
 * @date 2020/2/12 17:49
 */
public class JSONEncoder extends MessageToByteEncoder {
    /**
     * 目标对象类型进行编码
     */
    private Class<?> target;

    public JSONEncoder(Class target) {
        this.target = target;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (target.isInstance(msg)) {
            //使用fastJson将对象转换为byte
            byte[] data = JSON.toJSONBytes(msg);
            //先将消息长度写入，也就是消息头
            out.writeInt(data.length);
            //消息体中包含我们要发送的数据
            out.writeBytes(data);
        }
    }
}
