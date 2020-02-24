package com.hhu.server;

import com.hhu.codec.PacketCodecHandler;
import com.hhu.protocol.response.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class LogEventBroadcaster {

    private final EventLoopGroup group ;

    private final Bootstrap bootstrap;

    private final File file;

    private LogEventBroadcaster(final InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                /*.handler(new LogEventEncoder(address));*/
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new PacketCodecHandler(address));
                    }
                });
        this.file = file;
    }

    private void run() throws IOException {
        Channel channel = bootstrap.bind(0).syncUninterruptibly().channel();
        long pointer = 0;
        while (true) {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(pointer);
            String line;
            while ((line = raf.readLine()) != null) {
                 channel.writeAndFlush(new LogEvent(null, -1, file.getName(), new String(line.getBytes("ISO-8859-1"),"utf-8")));
            }
            pointer = raf.getFilePointer();
            raf.close();
            // 每秒检查一次文件内容
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    private void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 9999;
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(
                new InetSocketAddress("255.255.255.255", port),
                new File("D:\\git repo\\Netty-UDP-master\\src\\main\\java\\com\\hhu\\log.txt"));
        try {
            System.out.println("Broadcaster running");
            broadcaster.run();
        } catch (IOException e) {
            broadcaster.stop();
        }
    }



}
