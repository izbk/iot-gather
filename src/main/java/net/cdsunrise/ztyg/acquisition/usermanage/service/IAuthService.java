package net.cdsunrise.ztyg.acquisition.usermanage.service;

import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.TokenVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserInfoVo;

public interface IAuthService {

    Token generateToken(String clientId, String clientSecret, String requestTime) ;

    UserInfoVo getUserInfo(String token) ;

    UserInfoVo login(String token, String username, String password) ;

    TokenVo login(String username, String password);

    Boolean logout(String token) ;

    Boolean checkAuth(String token, String method, String path) ;

    String findKeyByToken(String token) ;
}
