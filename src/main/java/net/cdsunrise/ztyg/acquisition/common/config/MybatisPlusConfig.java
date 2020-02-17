package net.cdsunrise.ztyg.acquisition.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 配置
 * @author binke zhang
 * @date 2019-8-27 15:46:50
 */
@Configuration
@MapperScan(value = {
        "net.cdsunrise.ztyg.acquisition.vrv.mapper",
        "net.cdsunrise.ztyg.acquisition.meter.mapper",
        "net.cdsunrise.ztyg.acquisition.base.mapper",
        "net.cdsunrise.ztyg.acquisition.ils.mapper",
        "net.cdsunrise.ztyg.acquisition.sensor.mapper",
        "net.cdsunrise.ztyg.acquisition.hikvision.mapper",
        "net.cdsunrise.ztyg.acquisition.usermanage.mapper"
        })
public class MybatisPlusConfig {
    /**
     * SQL分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
