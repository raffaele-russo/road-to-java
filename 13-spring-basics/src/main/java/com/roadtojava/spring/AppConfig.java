package com.roadtojava.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * @Configuration + @ComponentScan: the annotation-based equivalent of an XML
 * bean definition file. ComponentScan tells Spring which package to scan for
 * @Component/@Service/@Repository classes; @Bean methods below register
 * beans manually (typically for third-party classes you can't annotate).
 */
@Configuration
@ComponentScan(basePackages = "com.roadtojava.spring")
public class AppConfig {

    // Required for @Value("${...}") placeholders to resolve; must be static
    // so it runs before other bean post-processing.
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // @Bean: manual registration, useful for classes you don't own (can't add
    // @Component to java.time.Clock).
    @Bean
    Clock systemClock() {
        return Clock.systemUTC();
    }
}
