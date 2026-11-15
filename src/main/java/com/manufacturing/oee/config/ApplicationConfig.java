package com.manufacturing.oee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneOffset;

/**
 * Core application infrastructure configuration.
 * Configures base beans such as the testable UTC clock.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Standard UTC clock bean for deterministic time accounting across shift calculations.
     *
     * @return UTC Clock
     */
    @Bean
    public Clock utcClock() {
        return Clock.system(ZoneOffset.UTC);
    }
}
