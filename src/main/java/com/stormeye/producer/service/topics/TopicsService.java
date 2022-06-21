package com.stormeye.producer.service.topics;

import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Contains interactions with the kafka topics
 * Maps to Casper Event Types
 */
@Service
public class TopicsService {

    public boolean hasTopic(final String event) {
        for(Topics topic : Topics.values()) {
            if (event.contains(topic.toCamelCase(topic))) {
                return true;
            }
        }

        return false;
    }

    public Optional<String> getTopic(final String event) {
        for(Topics topic : Topics.values()) {
            if (event.contains(topic.toCamelCase(topic))) {
                return Optional.ofNullable(topic.toCamelCase(topic));
            }
        }

        return Optional.empty();
    }

}
