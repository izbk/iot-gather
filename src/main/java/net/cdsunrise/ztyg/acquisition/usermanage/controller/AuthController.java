package net.cdsunrise.ztyg.acquisition.usermanage.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Client;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IAuthService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IClientService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.ITokenService;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.GenerateTokenVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.TokenVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

/**
 * @author Binke Zhang
 * @ClassName: AuthController
 * @Description: 权限控制器
 * @date 2017年4月25日 下午5:30:55
 */
@RestController
@RequestMapping(value = "auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IClientService clientService;

    /**
     * @return
     * @Title getCurrentTime
     * @Description 获取当前系统时间
     */
    @GetMapping(value = "getCurrentTime")
    public Result<Long> getCurrentTime() {
        Result<Long> result = ResultUtil.success(Calendar.getInstance().getTimeInMillis());
        return result;
    }

    /**
     * @param clientId：客户端ID
     * @return
     * @Title getClient
     * @Description 获取Client
     */
    @GetMapping(value = "getClient")
    public Result<Client> getClient(@RequestParam(value="clientId") String clientId) {
    	Result<Client> result = ResultUtil.success(clientService.getByClientId(clientId));
    	return result;
    }
    
    /**
     * @param vo   clientId：客户端ID， clientSecret：客户端密码， requestTime： 请求时间
     * @return
     * @Title getToken
     * @Description 获取Token
     */
    @PostMapping(value = "getToken")
    public Result<Token> getToken(@RequestBody GenerateTokenVo vo){
        return ResultUtil.success(this.authService.generateToken(vo.getClientId(), vo.getClientSecret(), vo.getRequestTime()));
    }

/*    *//**
     * @param token  RequestHeader参数，token：客户端有效验证码
     * @param vo username：用户名， password：用户密码
     * @return
     * @Title login
     * @Description 用户登录
     *//*
    @PostMapping(value = "login")
    public Result<UserInfoVo> login(@RequestHeader(name = "token") String token, @RequestBody LoginVo vo) {
        return ResultUtil.success(authService.login(token, vo.getUsername(), vo.getPassword()));
    }*/

    /**
     * @param username：用户名
     * @param password：用户密码
     * @return
     * @Title login
     * @Description 用户登录
     */
    @PostMapping(value = "login")
    public Result<TokenVo> login(@RequestParam(value = "username") String username,@RequestParam(value = "password") String password) {
        return ResultUtil.success(authService.login(username, password));
    }

    /**
     * @param token
     * @return
     * @Title logout
     * @Description 用户登出
     */
    @PostMapping(value = "logout")
    public Result<Boolean> logout(@RequestHeader(name = "token") String token) {
        return ResultUtil.success(authService.logout(token));
    }

    /**
     * @param token
     * @return
     * @Title getUserInfo
     * @Description 根据Token获取用户信息
     */
    @GetMapping(value = "getUserInfo")
    public Result<UserInfoVo> getUserInfo(@RequestHeader(name = "token") String token) {
        return ResultUtil.success(this.authService.getUserInfo(token));
    }

    @GetMapping(value = "findUserIdByToken")
    public Result<Long> findUserIdByToken(@RequestParam("token") String token) {
        return ResultUtil.success(tokenService.findUserIdByToken(token));
    }

}
