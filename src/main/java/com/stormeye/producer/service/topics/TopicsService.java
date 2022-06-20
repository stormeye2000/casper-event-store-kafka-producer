package com.stormeye.producer.service.topics;

import org.springframework.stereotype.Service;


/**
 * Contains interactions with the kafka topics
 * Maps to Casper Event Types
 */
@Service
public class TopicsService {

    public boolean hasTopic(final String event) {
        for(Topics topic : Topics.values()) {
            if (event.contains(topic.toString())) {
                return true;
            }
        }

        return false;
    }

    public String getTopic(final String event) {
        for(Topics topic : Topics.values()) {
            if (event.contains(topic.toString())) {
                return topic.toString();
            }
        }

        return null;
    }

}
