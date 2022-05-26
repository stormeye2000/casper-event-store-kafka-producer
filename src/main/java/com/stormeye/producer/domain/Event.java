package com.stormeye.producer.domain;


/**
 * Wraps the event and the emitter url together
 */
public class Event {

    private final String emitter;
    private final String event;

    public Event(final String emitter, final String event) {
        this.emitter = emitter;
        this.event = event;
    }

    @Override
    public String toString() {
        return "Event{" +
                "emitter:'" + emitter + '\'' +
                ", event:'" + event + '\'' +
                '}';
    }
}
