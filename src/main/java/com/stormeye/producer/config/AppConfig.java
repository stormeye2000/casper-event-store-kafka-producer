package com.stormeye.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configure any beans needed
 * Configurations are split out of the main SpringBoot class
 * to enable individual service testing
 */
@Configuration
public class AppConfig {

    @Bean
    public RetryTemplate getRetryTemplate() {

        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new EmitterRetry());

        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000L);

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);

        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
