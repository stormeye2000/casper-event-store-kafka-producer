package com.stormeye.producer.service.emitter;

import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.casper.sdk.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.stream.Stream;

/**
 * Simple service to connect to a single event emitter
 * over HTTP
 * connect method will retry n times
 */
@Service
public class EmitterService {

    private final Logger logger = LoggerFactory.getLogger(EmitterService.class.getName());

    @Retryable(maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000L, multiplier = 2, maxDelay = 60000L))
    public Stream<Event<String>> emitterStream(final URI emitter, final EventType eventType) {

        logger.debug("emitterStream for {} {}", emitter, eventType);
        final EventService eventService = EventService.usingPeer(emitter);
        return eventService.readEventStream(eventType, EventTarget.RAW, 0L);
    }

}
