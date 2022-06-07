package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;

@SpringBootTest(classes = {EmitterService.class, TopicsService.class, ServiceProperties.class, AppConfig.class})
@EnableConfigurationProperties(value = {ServiceProperties.class})
@EnableAutoConfiguration
public class TestTopicService {
    @Autowired
    private TopicsService topics;

    @Test
    void TestIsValidTopic(){

        assertTrue(topics.hasTopic("data:{\"FinalitySignature\":{\"block_hash\":\"eb608315c358992c60b91649c36756020f332acca960db93dfbbb139cbe33183\",\"era_id\":4822,\"signature\":\"01bebacca5cd3604042ba341a111f5398f3526a0131776a26f10ea70771a47d81c3014e85d90160c59f438d95b7c331246217a1fef7d403f0cd02dfeb31c746f07\",\"public_key\":\"01bbc59027ccfbba6c5c07d395b488c1f9d7515a23050fd9ffc9be800c729711fa\"}}\n"));

    }

   @Test
   void TestIsNotValidTopic(){

        assertFalse(topics.hasTopic("data:{:{\"block_hash\":\"eb608315c358992c60b91649c36756020f332acca960db93dfbbb139cbe33183\",\"era_id\":4822,\"signature\":\"01bebacca5cd3604042ba341a111f5398f3526a0131776a26f10ea70771a47d81c3014e85d90160c59f438d95b7c331246217a1fef7d403f0cd02dfeb31c746f07\",\"public_key\":\"01bbc59027ccfbba6c5c07d395b488c1f9d7515a23050fd9ffc9be800c729711fa\"}}\n"));

    }

   @Test
   void TestGetValidTopic(){

       assertEquals("FinalitySignature", topics.getTopic("data:{\"FinalitySignature\":{\"block_hash\":\"eb608315c358992c60b91649c36756020f332acca960db93dfbbb139cbe33183\",\"era_id\":4822,\"signature\":\"01bebacca5cd3604042ba341a111f5398f3526a0131776a26f10ea70771a47d81c3014e85d90160c59f438d95b7c331246217a1fef7d403f0cd02dfeb31c746f07\",\"public_key\":\"01bbc59027ccfbba6c5c07d395b488c1f9d7515a23050fd9ffc9be800c729711fa\"}}\n"));

   }

   @Test
   void TestGetInValidTopic(){

       assertNull(topics.getTopic("data:{\":{\"block_hash\":\"eb608315c358992c60b91649c36756020f332acca960db93dfbbb139cbe33183\",\"era_id\":4822,\"signature\":\"01bebacca5cd3604042ba341a111f5398f3526a0131776a26f10ea70771a47d81c3014e85d90160c59f438d95b7c331246217a1fef7d403f0cd02dfeb31c746f07\",\"public_key\":\"01bbc59027ccfbba6c5c07d395b488c1f9d7515a23050fd9ffc9be800c729711fa\"}}\n"));
       assertNull(topics.getTopic(""));

   }

}
