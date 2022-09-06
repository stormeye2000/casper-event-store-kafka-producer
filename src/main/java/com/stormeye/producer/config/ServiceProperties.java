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
    private List<Topic> topics;

    public List<URI> getEmitters() {
        return emitters;
    }

    public void setEmitters(final List<URI> emitters) {
        this.emitters = emitters;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(final List<Topic> topics) {
        this.topics = topics;
    }

    public static class Topic {
        private String topic;
        private int partitions;
        private int replicas;

        public String getTopic() {
            return topic;
        }

        public void setTopic(final String topic) {
            this.topic = topic;
        }

        public int getPartitions() {
            return partitions;
        }

        public void setPartitions(final int partitions) {
            this.partitions = partitions;
        }

        public int getReplicas() {
            return replicas;
        }

        public void setReplicas(final int replicas) {
            this.replicas = replicas;
        }
    }


}
