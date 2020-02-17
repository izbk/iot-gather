package net.cdsunrise.ztyg.acquisition.usermanage.component;

import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.usermanage.constants.CacheKey;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/25 11:48
 */
@Component
public class CheckTokenTask {

    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private IAuthService authService;
    private final CommonCache<String, Object> tokenCache = CommonCache.getInstance();

    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduled(){
        tokenCache.forEach((k,v)->{
            if(k.startsWith(CacheKey.TOKEN_KEY)){
                Token token = (Token) v;
                if(isTimeout(token.getLastAccessTime())){
                    authService.logout(token.getToken());
                }
            }
        });
    }

    /**
     * 通过最后一次访问时间计算过期时间
     *
     * @Title calcTimeout
     * @Description
     * @param lastAccessTime
     * @return
     *
     */
    private boolean isTimeout(Date lastAccessTime) {
        return (tokenConfig.getTokenExpire()
                - (Calendar.getInstance().getTimeInMillis() - lastAccessTime.getTime())) <=0 ;
    }
}
