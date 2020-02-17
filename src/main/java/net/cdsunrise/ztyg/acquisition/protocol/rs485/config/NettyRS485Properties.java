package net.cdsunrise.ztyg.acquisition.protocol.rs485.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: LiuWei
 * @date: Create in 16:39 2018/03/21
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty.rs485")
public class NettyRS485Properties {

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
