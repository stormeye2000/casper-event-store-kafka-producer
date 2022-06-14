package com.stormeye.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
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
    public RetryTemplate getInitialRetryTemplate() {

        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new HttpEmitterConnectionRetry());

        final ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setMaxInterval(50000L);
        exponentialBackOffPolicy.setInitialInterval(5000L);
        exponentialBackOffPolicy.setMultiplier(2);

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);

        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }


}
