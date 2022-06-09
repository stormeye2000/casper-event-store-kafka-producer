package com.stormeye.producer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

/**
 * Overridden methods in the Retry Template to log errors
 * Initial connection to an emitter node is passed through this template
 */
public class EmitterRetry extends RetryListenerSupport {

    private final Logger log = LoggerFactory.getLogger(RetryListenerSupport.class.getName());

    @Override
    public <T, E extends Throwable> void onError(final RetryContext context, final RetryCallback<T, E> callback, final Throwable throwable) {
        log.warn("Error connecting to emitter: {}", throwable.getMessage());
        super.onError(context, callback, throwable);
    }

}
