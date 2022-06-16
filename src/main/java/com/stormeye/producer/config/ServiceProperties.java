package com.stormeye.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Reads the application.yml properties
 * Enables lists of emitters and topics
 */
@Component("ServiceProperties")
@Configuration
@ConfigurationProperties(prefix = "services")
@EnableConfigurationProperties
public class ServiceProperties {
    private List<URI> emitters;
    private Kafka kafka;

    public List<URI> getEmitters() {
        return emitters;
    }

    public void setEmitters(final List<URI> emitters) {
        this.emitters = emitters;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(final Kafka kafka) {
        this.kafka = kafka;
    }

    public final static class Kafka {
        private List<String> topics;

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(final List<String> topics) {
            this.topics = topics;
        }
    }
}
