package net.cdsunrise.ztyg.acquisition.protocol.rs485.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.codec.Decoder4LoggingOnly;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.handler.RS485ClientHandler;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.handler.RS485ServerHandler;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.handler.VrvClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * netty 配置启动中心
 *
 * @author: LiuWei
 * @date: Create in 10:55 2018/08/24
 */
@Slf4j
@Configuration
public class NettyConfig {
    @Autowired
    RS485ServerHandler rs485ServerHandler;
    @Autowired
    RS485ClientHandler rs485ClientHandler;
    @Autowired
    VrvClientHandler vrvClientHandler;
    @Value("${gather.vrv.ip}")
    private String vrvIp;
    @Value("${gather.vrv.port}")
    private String vrvPort;

    @Value("${gather.ils.ip}")
    private String ilsIp;
    @Value("${gather.ils.port}")
    private String ilsPort;
    @Autowired
    private NettyRS485Properties rs485Properties;

    /**
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "netty.rs485.enable", havingValue = "true")
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

    /**
     *
     * @return
     */
    @Bean("vrvBootStrap")
    @SuppressWarnings("Duplicates")
    public Bootstrap startVrvBootStrap() {
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
                        pipeline.addLast(vrvClientHandler);
                    }
                });
        return bootstrap;
    }
}
