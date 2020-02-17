package net.cdsunrise.ztyg.acquisition.protocol.rs485;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author: LiuWei
 * @date: Create in 17:15 2018/03/21
 */
@Slf4j
public class NettySocketClientDriver {

    /**
     * 用于分配处理业务线程的线程组个数
     */
    public static void main(String[] args) throws Exception{
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //获取管道
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(5, 0, 0));
                        pipeline.addLast(new ClientHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .option(ChannelOption.SO_SNDBUF, 10 * 1024 * 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        //发起异步连接操作
        ChannelFuture futrue = bootstrap.connect(new InetSocketAddress("127.0.0.1", 9003)).sync();
        //等待客户端链路关闭
        Channel channel = futrue.channel();
        channel.closeFuture().sync();
    }
}
