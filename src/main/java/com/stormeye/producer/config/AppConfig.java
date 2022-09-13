package com.stormeye.producer.config;

import static java.util.Map.entry;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import com.stormeye.producer.json.CsprEventSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import reactor.kafka.sender.SenderOptions;

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


    @Qualifier("ServiceProperties") final ServiceProperties properties;

    public AppConfig(@Qualifier("ServiceProperties") final ServiceProperties properties) {
        this.properties = properties;
    }


    @Bean
    public ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate() {
        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerConfigs()).maxInFlight(1024);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public List<NewTopic> newTopics() {

        final List<NewTopic> newTopics = new ArrayList<>();

        properties.getTopics().forEach( t ->
            newTopics.add(
                    TopicBuilder.name(t.getTopic())
                            .partitions(t.getPartitions())
                            .replicas(t.getReplicas())
                            .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip")
                            .build()
                )
        );

        return newTopics;
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
