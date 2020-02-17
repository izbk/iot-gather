package net.cdsunrise.ztyg.acquisition.protocol.tcp.server.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.codec.Decoder4LoggingOnly;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.server.handler.RS485ServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * netty server 配置
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Slf4j
@Configuration
public class NettyServerConfig {
    @Autowired
    RS485ServerHandler rs485ServerHandler;
    @Autowired
    private NettyServerProperties rs485Properties;

    /**
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "netty.tcp.enable", havingValue = "true")
    public ServerBootstrap startRS485ServerBootStrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(rs485Properties.getBossThreadNum()),
                new NioEventLoopGroup(rs485Properties.getWorkerThreadNum()))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new Decoder4LoggingOnly());
                        // 加入心跳
                        pipeline.addLast(new IdleStateHandler(rs485Properties.getReaderIdleTimeSeconds(),
                                rs485Properties.getWriterIdleTimeSeconds(),
                                rs485Properties.getAllIdleTimeSeconds()));
                        pipeline.addLast(rs485ServerHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, rs485Properties.getSoBackLog())
                .option(ChannelOption.SO_RCVBUF, rs485Properties.getSoRcvbuf())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_SNDBUF, rs485Properties.getSoSndbuf())
                .childOption(ChannelOption.TCP_NODELAY, rs485Properties.isTcpNoDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, rs485Properties.isSoKeepalive());

        start(serverBootstrap, rs485Properties.getHost(), rs485Properties.getTcpPort());
        return serverBootstrap;
    }

    public void start(ServerBootstrap serverBootstrap, String host, Integer port) {
        new Thread(() -> {
            try {
                log.info("socker server start host: {} , port: {}", host, port);
                ChannelFuture channelFuture;
                if (host != null) {
                    channelFuture = serverBootstrap.bind(host, port).sync();
                } else {
                    channelFuture = serverBootstrap.bind(port).sync();
                }
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("create socket error", e);
            }
        }).start();
    }
}
