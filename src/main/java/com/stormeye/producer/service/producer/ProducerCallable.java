package com.stormeye.producer.service.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import com.stormeye.producer.domain.Event;
import com.stormeye.producer.exceptions.EmitterStoppedException;
import com.stormeye.producer.service.topics.TopicsService;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

/**
 * Callable producer class that enables concurrency on the http emitters
 * Also exposes exception handling to the calling method
 */
public class ProducerCallable implements Callable<Object> {
    private final Logger log = LoggerFactory.getLogger(ProducerCallable.class.getName());
    private static final Integer MAX_RANGE = 1;

    private final TopicsService topicsService;
    private final URI emitterUri;
    private final Stream<String> emitterStream;

    private final ReactiveKafkaProducerTemplate<Integer, String> template;

    public ProducerCallable(final ReactiveKafkaProducerTemplate<Integer, String> template, final TopicsService topicsService, final URI emitterUri, final Stream<String> emitterStream){
        this.topicsService = topicsService;
        this.emitterUri = emitterUri;
        this.template = template;
        this.emitterStream = emitterStream;
    }

    @Override
    public Object call() {
        try {

            emitterStream.forEach(

                    event -> {

                        if (topicsService.hasTopic(event)){

                            final Optional<String> topic = topicsService.getTopic(event);

                            if (topic.isPresent()) {

                                log.debug("Emitter: [{}] Topic: [{}] - Event : {}", emitterUri.toString(), topic.get(), event);

                                final Flux<SenderRecord<Integer, String, Integer>> outboundFlux = Flux.range(0, MAX_RANGE)
                                        .map(i ->
                                                SenderRecord.create(topic.get(), 0,
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

            throw new EmitterStoppedException(String.format("Emitter [%s] stopped.", emitterUri));

        } catch (Exception e){
            throw new EmitterStoppedException(e.getMessage());
        }
    }
}
