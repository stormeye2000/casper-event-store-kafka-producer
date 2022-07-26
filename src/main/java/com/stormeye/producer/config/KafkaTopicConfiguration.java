package com.stormeye.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfiguration {

    final static int PARTITIONS = 5;
    final static int REPLICAS = 3;

    @Bean
    public NewTopic topicDeployProcessed() {
        return TopicBuilder.name("DeployProcessed").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicDeployExpired() {
        return TopicBuilder.name("DeployExpired").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicBlockAdded() {
        return TopicBuilder.name("BlockAdded").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicDeployAccepted() {
        return TopicBuilder.name("DeployAccepted").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicFinalitySignature() {
        return TopicBuilder.name("FinalitySignature").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicStep() {
        return TopicBuilder.name("Step").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicFault() {
        return TopicBuilder.name("Fault").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicApiVersion() {
        return TopicBuilder.name("ApiVersion").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicShutdown() {
        return TopicBuilder.name("Shutdown").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
}