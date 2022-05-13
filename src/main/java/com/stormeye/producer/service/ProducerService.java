package com.stormeye.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.stormeye.producer.config.KafkaProducer;
import com.stormeye.producer.config.ServiceProperties;

import reactor.kafka.sender.KafkaSender;

/**
 * Service to start the kafka producer
 * Each emitter from the properties file runs in its own thread
 */
@Service
public class ProducerService {

    private static final Logger log = LoggerFactory.getLogger(ProducerService.class.getName());

    private final ServiceProperties properties;

    public ProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties) {
        this.properties = properties;
    }

    public void startEventConsumers() {
        final KafkaSender<Integer, String> kafkaProducer = new KafkaProducer(properties).getSender();

        properties.getEmitters().forEach(
                emitter -> {
                    log.info("Starting kafka producer for casper event emitter: [{}]", emitter);
                    new ProducerThread(new HttpService(emitter), properties.getKafka().getTopics(), kafkaProducer).start();
                }
        );

    }



}
