package com.stormeye.producer.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Performs a simple query on the kafka broker
 * If there's no connection will throw an exception
 */
@Component
public class BrokerState {

    private final Logger logger = LoggerFactory.getLogger(BrokerState.class.getName());
    private final AdminClient adminClient;

    public BrokerState(final AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public boolean isAvailable() {
        try {
            final ListTopicsResult topics = adminClient.listTopics();
            topics.names().get();
            return true;
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return false;
        }

    }



}
