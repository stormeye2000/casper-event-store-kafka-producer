package com.stormeye.producer.config;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

public class KafkaProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String CLIENT_ID_CONFIG = "stormeye-kafka";

    private final KafkaSender<Integer, String> sender;

    public KafkaProducer() {

        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        final SenderOptions<Integer, String> senderOptions = SenderOptions.<Integer, String>create(producerProps).maxInFlight(1024);

        this.sender = KafkaSender.create(senderOptions);

    }

    public KafkaSender<Integer, String> getSender() {
        return sender;
    }
}
