package com.stormeye.producer.config;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import com.stormeye.producer.exceptions.KafkaProducerException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class that captures log output into a ByteArrayOutputStream
 */
public class TestLogAppender implements Appender {
    private Filter filter;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private ErrorHandler errorHandler;
    private Layout layout;
    private String name = TestLogAppender.class.getSimpleName();

    public void reset() {
        try {
            baos.close();
        } catch (IOException e) {
            throw new KafkaProducerException(e);
        }
        baos = new ByteArrayOutputStream();
    }


    @Override public void addFilter(final Filter newFilter) {
        this.filter = newFilter;
    }
    @Override public Filter getFilter() {
        return filter;
    }
    @Override public void clearFilters() {
    }
    @Override public void close() {
        try {
            baos.close();
        } catch (IOException e) {
            throw new KafkaProducerException(e);
        }
    }
    @Override public void doAppend(final LoggingEvent event) {
        try {
            baos.write(event.getMessage().toString().getBytes());
        } catch (IOException e) {
            throw new KafkaProducerException(e);
        }
    }
    @Override public String getName() {
        return this.name;
    }
    @Override public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    @Override public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    @Override public void setLayout(final Layout layout) {
        this.layout = layout;
    }
    @Override public Layout getLayout() {
        return this.layout;
    }
    @Override public void setName(final String name) {
        this.name = name;
    }
    @Override public boolean requiresLayout() {
        return false;
    }
    @Override public String toString() {
        return baos.toString();
    }
}