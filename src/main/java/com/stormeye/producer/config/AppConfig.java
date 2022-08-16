package com.stormeye.producer.config;

import com.stormeye.producer.json.CsprEventSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Configure any beans needed
 * Configurations are split out of the main SpringBoot class
 * to enable individual service testing
 */
@Configuration
public class AppConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.client-id}")
    private String clientId;


    @Bean
    public ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate() {
        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerConfigs()).maxInFlight(1024);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    private Map<String, Object> producerConfigs() {
        return Map.ofEntries(
                entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                entry(ProducerConfig.CLIENT_ID_CONFIG, clientId),
                entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CsprEventSerializer.class)
        );
    }
}
