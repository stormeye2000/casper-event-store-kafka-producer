package com.stormeye.producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
import reactor.kafka.sender.SenderOptions;

/**
 * Configure any beans needed
 * Configurations are split out of the main SpringBoot class
 * to enable individual service testing
 */
@Configuration
public class AppConfig {

    @Value("${spring.kafka.producer.client-id}")
    private String clientId;

    @Value("${KAFKA_BOOTSTRAP_SERVER:localhost:9092}")
    private String bootstrapServers;

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


    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate(){
        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerConfigs()).maxInFlight(1024);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

}
