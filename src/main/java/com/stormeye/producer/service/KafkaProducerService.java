package com.stormeye.producer.service;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import com.stormeye.producer.config.ServiceProperties;

import java.util.HashMap;
import java.util.Map;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

/**
 * Kafka Producer start up configuration
 */
public class KafkaProducerService {

    private final KafkaSender<Integer, String> sender;

    private final ServiceProperties properties;

    public KafkaProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties) {
        this.properties = properties;

        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().getServer() + ":" + properties.getKafka().getPort());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        //The consumer needs this client_id as the group_id i think
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getKafka().getClient());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerProps).maxInFlight(1024);

        this.sender = KafkaSender.create(senderOptions);

    }


    public Map<String, Object> getProperties() {
        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().getServer() + ":" + properties.getKafka().getPort());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        //The consumer needs this client_id as the group_id i think
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getKafka().getClient());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return producerProps;
    }
}
