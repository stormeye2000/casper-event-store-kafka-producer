package com.stormeye.producer.service.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.service.emitter.EmitterService;
import com.stormeye.producer.service.topics.TopicsService;

import java.util.concurrent.ExecutorService;
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
    private final TopicsService topicsService;
    private final RetryTemplate initialRetryTemplate;
    private final ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate;

    public ProducerService(@Qualifier("ServiceProperties") final ServiceProperties properties, final EmitterService emitterService, final TopicsService topicsService,
                           final RetryTemplate initialRetryTemplate, final ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate) {
        this.properties = properties;
        this.emitterService = emitterService;
        this.topicsService = topicsService;
        this.initialRetryTemplate = initialRetryTemplate;
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void startEventConsumers() {

        try {

            final ExecutorService executor = Executors.newCachedThreadPool();

            properties.getEmitters().forEach(
                    emitter -> {

                        RetryContext context = null;
                        try {
                            context = initialRetryTemplate.execute(ctx -> {
                                emitterService.connect(emitter);
                                return ctx;
                            });
                        } catch (Exception e) {
                            log.error("Failed to connect to emitter [{}] after retries, {}", emitter, e.getMessage());
                        }

                        if (context != null && !context.isExhaustedOnly()) {

                            log.info("Successfully connected to emitter: [{}]", emitter);
                            log.info("Starting kafka producer for casper event emitter: [{}]", emitter);

                            executor.submit(new ProducerCallable(reactiveKafkaProducerTemplate, topicsService, emitter, emitterService.emitterStream(emitter)));

                        }
                    }
            );
        } catch (Exception e){}

    }
}
