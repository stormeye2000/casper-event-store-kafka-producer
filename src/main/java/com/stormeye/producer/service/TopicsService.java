package com.stormeye.producer.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.stormeye.producer.config.ServiceProperties;

/**
 * Contains interactions with the kafka topics
 * Maps to Casper Event Types
 */
@Service
public class TopicsService {

    private final ServiceProperties serviceProperties;

    public TopicsService(@Qualifier("ServiceProperties") final ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public boolean hasTopic(final String event) {
        return serviceProperties.getKafka()
                .getTopics()
                .stream()
                .anyMatch(event::contains);
    }

    public String getTopic(final String event) {
        return serviceProperties.getKafka()
                .getTopics()
                .stream()
                .filter(event::contains)
                .findAny()
                .orElse(null);
    }

}
