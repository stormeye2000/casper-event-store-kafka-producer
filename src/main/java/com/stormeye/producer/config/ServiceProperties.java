package com.stormeye.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
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

    public static class Kafka{
        private String server;
        private String port;
        private String client;
        private List<String> topics;

        public String getServer() {
            return server;
        }

        public String getPort() {
            return port;
        }

        public String getClient() {
            return client;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setServer(final String server) {
            this.server = server;
        }

        public void setPort(final String port) {
            this.port = port;
        }

        public void setClient(final String client) {
            this.client = client;
        }

        public void setTopics(final List<String> topics) {
            this.topics = topics;
        }
    }




}
