package com.stormeye.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.producer.config.Topics;
import com.stormeye.producer.domain.EventTopic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

public class ReceiverService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ReceiverService.class.getName());

    final private List<Topics> topics;
    final KafkaSender<Integer, String> sender;

    private final HttpClient client;
    private final HttpRequest request;

    public ReceiverService(final String emitter, final List<Topics> topics, final KafkaSender<Integer, String> sender) throws URISyntaxException {
        this.topics = topics;
        this.sender = sender;

        final URI uri = new URI(emitter);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(uri).GET().build();

        log.info("Starting receiver for emitter: " + emitter);

    }

    public void run() {

        final ObjectMapper mapper = new ObjectMapper();

        try {

            client.send(request, HttpResponse.BodyHandlers.ofLines()).body().forEach(

                    event -> {

                        if (event.length() > 35){

                            log.info("Event : " + event);

                            final Topics eventTopic = new EventTopic(event, mapper).getTopic();
                            boolean eventSent = false;

                            for(Topics topic : topics){
                                if (topic.equals(eventTopic)){

                                    log.info("Topic : " + topic);

                                    final Flux<SenderRecord<Integer, String, Integer>> outboundFlux = Flux.range(0, 1)
                                            .map(i -> SenderRecord.create(topic.toString(), 0, new Date().getTime(), i, event, i));

                                    sender.send(outboundFlux)
                                            .doOnError(e-> log.error("Send failed", e))
                                            .doOnNext(r -> System.out.printf("Message #%d send response: %s\n", r.correlationMetadata(), r.recordMetadata()))
                                            .subscribe();
                                    eventSent = true;

                                }
                            }

                            if (!eventSent){
                                log.error("Failed to send event to topic: " + eventTopic);
                            }

                        }


                    }

            );

        } catch (IOException | InterruptedException e) {
           log.error(e.getMessage());
        }

    }



}
