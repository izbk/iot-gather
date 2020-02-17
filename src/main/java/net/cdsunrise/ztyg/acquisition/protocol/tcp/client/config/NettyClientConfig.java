package net.cdsunrise.ztyg.acquisition.protocol.tcp.client.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.client.handler.SyncClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * netty 客户端配置中心
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Slf4j
@Configuration
public class NettyClientConfig {
    @Autowired
    private SyncClientHandler rs485ClientHandler;
     @Bean("rs485BootStrap")
     @SuppressWarnings("Duplicates")
     public Bootstrap startRS485BootStrap() {
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
                         pipeline.addLast(rs485ClientHandler);
                     }
         });
         return bootstrap;
     }
}
