package com.stormeye.producer.service.emitter;

import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.casper.sdk.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.stormeye.producer.exceptions.EmitterStoppedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

/**
 * Simple service to connect to a single event emitter
 * over HTTP
 * connect method will retry n times
 */
@Service
public class EmitterService {

    private final Logger log = LoggerFactory.getLogger(EmitterService.class.getName());

    public Stream<Event<String>> emitterStream(final URI emitter, final EventType eventType) {
            final EventService eventService = EventService.usingPeer(emitter);
            return eventService.readEventStream(eventType, EventTarget.RAW, 0L);
            //return this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).body();
    }

    public void connect(final URI emitter) throws IOException, InterruptedException {
       log.info("Attempting to connect to [{}]", emitter);
        // TODO remove this
       //this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).headers();
    }

}
