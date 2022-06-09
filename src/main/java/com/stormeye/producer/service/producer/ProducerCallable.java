package com.stormeye.producer.service.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import com.stormeye.producer.domain.Event;
import com.stormeye.producer.exceptions.EmitterStoppedException;
import com.stormeye.producer.service.emitter.EmitterService;
import com.stormeye.producer.service.topics.TopicsService;

import java.net.URI;
import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

/**
 * Callable producer class that enables concurrency on the http emitters
 * Also exposes exception handling to the calling method
 */
public class ProducerCallable implements Callable<Object> {
    private final Logger log = LoggerFactory.getLogger(ProducerCallable.class.getName());
    private static final Integer MAX_RANGE = 1;

    private final EmitterService emitterService;
    private final TopicsService topicsService;
    private final URI emitterUri;

    private final ReactiveKafkaProducerTemplate<Integer, String> template;

    public ProducerCallable(final ReactiveKafkaProducerTemplate<Integer, String> template, final EmitterService emitterService, final TopicsService topicsService, final URI emitterUri){
        this.emitterService = emitterService;
        this.topicsService = topicsService;
        this.emitterUri = emitterUri;
        this.template = template;
    }

    @Override
    public Object call() {
        try {

            emitterService.emitterStream(emitterUri).forEach(

                    event -> {

                        if (topicsService.hasTopic(event)){

                            final String topic = topicsService.getTopic(event);

                            if (topic != null) {

                                log.debug("Emitter: [{}] Topic: [{}] - Event : {}", emitterUri.toString(), topic, event);

                                final Flux<SenderRecord<Integer, String, Integer>> outboundFlux = Flux.range(0, MAX_RANGE)
                                        .map(i ->
                                                SenderRecord.create(topicsService.getTopic(event), 0,
                                                        System.currentTimeMillis(),
                                                        i, new Event(emitterUri.toString(), event).toString(), i)
                                        );

                                template.send(outboundFlux)
                                        .doOnError(e-> {
                                            log.error("Send failed for event: {}", event);
                                            log.error("Error - {}", e .getMessage());
                                        })
                                        .subscribe();
                            } else {
                                log.error("Unknown topic for event - {}", event);
                            }

                        }
                    }
            );

            throw new EmitterStoppedException(String.format("Emitter [%s] ended.", emitterUri));

        } catch (Exception e){
            throw new EmitterStoppedException(e.getMessage());
        }
    }
}
