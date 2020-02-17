package net.cdsunrise.ztyg.acquisition.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Binke Zhang
 * @date 2019/9/12 12:25
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
    /**
     * 调用SSO拦截器
     */
    private final AuthInterceptor authInterceptor;

    @Autowired
    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login/**")
                .excludePathPatterns("/images/**")
                .order(1);
    }
}
