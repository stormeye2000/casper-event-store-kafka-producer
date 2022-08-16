package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.Event;
import com.stormeye.producer.exceptions.EmitterStoppedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Callable producer class that enables concurrency on the http emitters
 * Also exposes exception handling to the calling method
 */
public class ProducerCallable implements Callable<Object> {
    private final Logger logger = LoggerFactory.getLogger(ProducerCallable.class.getName());
    private static final Integer MAX_RANGE = 1;

    private final URI emitterUri;
    private final Stream<Event<String>> emitterStream;

    private final ReactiveKafkaProducerTemplate<Integer, String> template;

    public ProducerCallable(final ReactiveKafkaProducerTemplate<Integer, String> template,
                            final URI emitterUri,
                            final Stream<Event<String>> emitterStream) {
        this.emitterUri = emitterUri;
        this.template = template;
        this.emitterStream = emitterStream;
    }

    @Override
    public Object call() {
        try {

            emitterStream.forEach(

                    event -> {

                        var topic = event.getEventType().name().toLowerCase();

                        logger.debug("Emitter: [{}] Topic: [{}] - Event : {}", emitterUri, topic, event);

                        var outboundFlux = Flux.range(0, MAX_RANGE)
                                .map(i ->
                                        SenderRecord.create(topic,
                                                0,
                                                System.currentTimeMillis(),
                                                i,
                                                event,
                                                i)
                                );

                        //noinspection unchecked,rawtypes
                        template.send((Flux) outboundFlux)
                                .doOnError((Consumer<Throwable>) e -> logger.error("Send failed for event: " + event, e))
                                .subscribe();
                    }
            );

            throw new EmitterStoppedException(String.format("Emitter [%s] stopped.", emitterUri));

        } catch (Exception e) {
            throw new EmitterStoppedException(e.getMessage());
        }
    }


}
