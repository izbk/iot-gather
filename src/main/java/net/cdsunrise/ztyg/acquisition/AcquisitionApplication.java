package net.cdsunrise.ztyg.acquisition;

import net.cdsunrise.ztyg.acquisition.common.utils.NativeLoader;
import net.cdsunrise.ztyg.acquisition.dahua.module.LoginModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 采集中心
 *
 * @author binke zhang
 * @date 2019-08-01
 */
@SpringBootApplication
@EnableAsync
public class AcquisitionApplication implements CommandLineRunner {
    public static void main(String[] args) {
        try {
            NativeLoader.loader("libs/win64");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(AcquisitionApplication.class,args);
    }

    @Override
    public void run(String... args) {
        LoginModule.login("192.168.10.247",37777,"admin","admin1234");
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        final CorsConfiguration config = new CorsConfiguration();
        // 允许cookies跨域
        config.setAllowCredentials(true);
        // 允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        config.addAllowedOrigin("*");
        // 允许访问的头信息,*表示全部
        config.addAllowedHeader("*");
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(1800L);
        // 允许提交请求的方法，*表示全部允许
        config.addAllowedMethod("*");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(corsFilter);
        bean.setOrder(0);
        return bean;
    }
}
