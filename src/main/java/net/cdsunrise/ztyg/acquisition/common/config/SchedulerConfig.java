package net.cdsunrise.ztyg.acquisition.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author sh
 * @date 2019-12-26 16:57
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "schedule.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig {
}
