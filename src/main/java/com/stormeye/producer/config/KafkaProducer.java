package com.stormeye.producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

/**
 * Kafka Producer start up configuration
 */
public class KafkaProducer {

    private final KafkaSender<Integer, String> sender;

    public KafkaProducer(final ServiceProperties properties) {

        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().getServer() + ":" + properties.getKafka().getPort());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getKafka().getClient());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerProps).maxInFlight(1024);

        this.sender = KafkaSender.create(senderOptions);

    }

    public KafkaSender<Integer, String> getSender() {
        return sender;
    }
}
