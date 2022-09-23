package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventType;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.exceptions.EmitterStoppedException;
import com.stormeye.producer.service.emitter.EmitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Service to start the kafka producer
 * Each emitter will be tested connected via the retry template
 * Each emitter from the properties file then runs in its own thread
 */
@Service
public class ProducerService {

    private static final Integer MAX_RANGE = 1;
    private final Logger logger = LoggerFactory.getLogger(ProducerService.class.getName());
    private final ServiceProperties properties;
    private final EmitterService emitterService;
    private final IdStorageService idStorageService;
    private final ReactiveKafkaProducerTemplate<Integer, String> producerTemplate;

    public ProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties,
                           final EmitterService emitterService,
                           final IdStorageService idStorageService,
                           final ReactiveKafkaProducerTemplate<Integer, String> producerTemplate) {
        this.properties = properties;
        this.emitterService = emitterService;
        this.idStorageService = idStorageService;
        this.producerTemplate = producerTemplate;
    }

    public void startEventConsumers() {

        try {

            var executor = Executors.newCachedThreadPool();

            properties.getEmitters().forEach(
                    emitter ->
                            Arrays.stream(EventType.values()).forEach(eventType -> {
                                logger.info("Starting kafka producer for casper event [{}] emitter: [{}]", eventType, emitter);
                                executor.submit(() -> {
                                    try {
                                        emitterService.emitEvents(emitter, eventType, event -> sendEvent(emitter, event));
                                    } catch (Exception e) {
                                        throw new EmitterStoppedException(e.getMessage());
                                    }
                                });
                            })
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    void sendEvent(final URI emitter, final Event<?> event) {

        var topic = event.getEventType().name().toLowerCase();

        logger.debug("Emitter: [{}] Topic: [{}] - Event : [{}]", emitter, topic, event);

        var outboundFlux = Flux.range(0, MAX_RANGE)
                .map(i -> SenderRecord.create(topic, 0, System.currentTimeMillis(), i, event, i));

        //noinspection unchecked,rawtypes
        producerTemplate.send((Flux) outboundFlux)
                .doOnError((Consumer<Throwable>) e -> logger.error("Send failed for event: " + event, e))
                .subscribe();

        // Persist the ID of the event for playback
        event.getId().ifPresent(id -> idStorageService.setCurrentEvent(emitter.toString(), event.getEventType(), id));

    }
}
