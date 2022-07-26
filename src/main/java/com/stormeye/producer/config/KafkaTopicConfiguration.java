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
        return TopicBuilder.name("DEPLOY_PROCESSED").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicDeployExpired() {
        return TopicBuilder.name("DEPLOY_EXPIRED").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicBlockAdded() {
        return TopicBuilder.name("BLOCK_ADDED").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicDeployAccepted() {
        return TopicBuilder.name("DEPLOY_ACCEPTED").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicFinalitySignature() {
        return TopicBuilder.name("FINALITY_SIGNATURE").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicStep() {
        return TopicBuilder.name("STEP").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicFault() {
        return TopicBuilder.name("FAULT").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicApiVersion() {
        return TopicBuilder.name("APIVERSION").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
    @Bean
    public NewTopic topicShutdown() {
        return TopicBuilder.name("SHUTDWON").partitions(PARTITIONS).replicas(REPLICAS).build();
    }
}