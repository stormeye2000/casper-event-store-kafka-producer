package com.stormeye.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import com.stormeye.producer.domain.Event;
import com.stormeye.producer.exceptions.EmitterStoppedException;

import java.net.URI;
import java.util.Date;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

/**
 * Starts a kafka producer in its own thread
 * Events are read from the emitter and their event type is mapped to a kafka topic
 * Each event type is then written to its own corresponding topic
 */
class ProducerThread extends Thread{

    private static final Logger log = LoggerFactory.getLogger(ProducerThread.class.getName());
    private static final Integer MAX_RANGE = 1;

    private final EmitterService emitterService;
    private final TopicsService topicsService;
    private final URI emitterUri;

    private final ReactiveKafkaProducerTemplate<Integer, String> template;

    public ProducerThread(final ReactiveKafkaProducerTemplate<Integer, String> template, final EmitterService emitterService, final TopicsService topicsService, final URI emitterUri){
        this.emitterService = emitterService;
        this.topicsService = topicsService;
        this.emitterUri = emitterUri;
        this.template = template;
    }

    public void run() {

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
                                                        new Date().getTime(),
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

        } catch (EmitterStoppedException e) {
            throw e;
        } catch (Exception e){
            log.error(e.getMessage());
        }


    }

}
