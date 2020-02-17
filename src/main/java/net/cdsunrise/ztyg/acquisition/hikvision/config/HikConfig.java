package net.cdsunrise.ztyg.acquisition.hikvision.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author sh
 * @date 2019-12-11 11:58
 */
@Data
@Configuration
@ConfigurationProperties("hikvision")
public class HikConfig {
    private String host;
    private String appKey;
    private String appSecret;
    private String nativeIp;
    private Boolean enableSubscription;
    private String pictureAddress;

    @PostConstruct
    public void init() {
        ArtemisConfig.host = this.host; // artemis网关服务器ip端口
        ArtemisConfig.appKey = this.appKey;  // 秘钥appkey
        ArtemisConfig.appSecret = this.appSecret;// 秘钥appSecret
    }
}
