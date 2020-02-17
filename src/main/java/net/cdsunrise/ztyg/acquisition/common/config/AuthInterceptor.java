package net.cdsunrise.ztyg.acquisition.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Binke Zhang
 * @date 2019/12/4 9:57
 */
@Slf4j
@Configuration
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        /*String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
           new BusinessException(ExceptionEnum.PARAM_ERROR.getCode(),"token不能为空");
        }
        if(!authService.checkAuth(token,request.getMethod(),request.getRequestURI())){
            return false;
        }*/
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){

    }
}
