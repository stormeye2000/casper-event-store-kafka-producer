package com.stormeye.producer.exceptions;

public class KafkaProducerException extends RuntimeException {

    public KafkaProducerException(final Throwable cause) {
        super(cause);
    }
}
