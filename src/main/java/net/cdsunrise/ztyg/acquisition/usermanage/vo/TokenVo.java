package net.cdsunrise.ztyg.acquisition.usermanage.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.cdsunrise.common.utility.utils.JsonUtils;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:54
 */
@Data
@NoArgsConstructor
public class TokenVo {
    /**token，客户端进行请求时必须将token值放入header中进行请求*/
    private String token;
    /**用户ID*/
    private Long userId;
    /**客户端ID*/
    private Long clientId;
    /**上次访问时间*/
    private Date lastAccessTime;
    /**Token相关的用户信息*/
    private UserInfoVo userInfo;

    public TokenVo(Token domain) {
        this.token = domain.getToken();
        this.userId = domain.getUserId();
        this.clientId = domain.getClientId();
        this.lastAccessTime = domain.getLastAccessTime();
        this.userInfo = JsonUtils.toObject(domain.getUserInfo(), new TypeReference<UserInfoVo>() {});
    }
}
