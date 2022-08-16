package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.service.emitter.EmitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.Executors;


/**
 * Service to start the kafka producer
 * Each emitter will be tested connected via the retry template
 * Each emitter from the properties file then runs in its own thread
 */
@Service
public class ProducerService {

    private final Logger log = LoggerFactory.getLogger(ProducerService.class.getName());
    private final ServiceProperties properties;
    private final EmitterService emitterService;
    private final ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate;

    public ProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties,
                           final EmitterService emitterService,
                           final ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate) {
        this.properties = properties;
        this.emitterService = emitterService;
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void startEventConsumers() {

        try {
            var executor = Executors.newCachedThreadPool();

            properties.getEmitters().forEach(
                    emitter ->
                            Arrays.stream(EventType.values()).forEach(eventType -> {
                                log.info("Starting kafka producer for casper event [{}] emitter: [{}]", eventType, emitter);
                                executor.submit(new ProducerCallable(reactiveKafkaProducerTemplate, emitter, emitterService.emitterStream(emitter, eventType)));
                            })
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }


}
