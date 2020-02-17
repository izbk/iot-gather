package net.cdsunrise.ztyg.acquisition.protocol.tcp.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * netty server 属性
 * @author Binke Zhang
 * @date 2019/11/25 16:04
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty.tcp")
public class NettyServerProperties {

    private int bossThreadNum;

    private int workerThreadNum;

    private String host;

    private int tcpPort;

    private int soBackLog;

    private int soRcvbuf;

    private int soSndbuf;

    private boolean soKeepalive;

    private boolean tcpNoDelay;

    private int readerIdleTimeSeconds;

    private int writerIdleTimeSeconds;

    private int allIdleTimeSeconds;

    private String decoder;

    private String encoder;

    private String serverHandler;

}
