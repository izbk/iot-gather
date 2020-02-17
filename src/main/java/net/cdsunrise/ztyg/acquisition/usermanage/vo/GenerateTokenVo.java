package net.cdsunrise.ztyg.acquisition.usermanage.vo;

import lombok.Data;

/**
 * @author Binke Zhang
 * @date 2019/12/24 17:59
 */
@Data
public class GenerateTokenVo {
    private String clientId;
    private String clientSecret;
    private String requestTime;
}
