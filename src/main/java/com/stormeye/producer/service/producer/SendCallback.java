package com.stormeye.producer.service.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a callback for a Producer send event
 * Will trap any errors and report them to the log
 */
public class SendCallback implements Callback {
    private final Logger logger = LoggerFactory.getLogger(SendCallback.class.getName());

    @Override
    public void onCompletion(final RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            logger.error("Error producing event to topic: {}", recordMetadata);
        } else {
            logger.debug("Succesfully sent event to Topic: [{}]  Partition: [{}]  Offset: [{}]", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        }
    }
}
