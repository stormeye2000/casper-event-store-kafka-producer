package com.stormeye.producer.service.producer.send;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.casper.sdk.model.event.Event;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;

import java.util.concurrent.Future;

@SpringBootTest(classes = {AppConfig.class, ServiceProperties.class})
@EmbeddedKafka(topics = {"main"}, partitions = 1, ports = {9094}, brokerProperties = {"message.max.bytes=268435456"})
public class SendMegaEventSuccess extends SendMethods {
    @Autowired
    private KafkaProducer<Integer, Event<?>> kafkaProducer;

    @Test
    void testSendEvent() throws Exception {

        final Event<?> event = super.buildEvent(super.getEventFile("step-large.event"));

        kafkaProducer = new KafkaProducer<>(producerConfigs(MB256, "9094"));
        final ProducerRecord<Integer, Event<?>> producerRecord = new ProducerRecord<>(TOPIC, event);

        final Future<RecordMetadata> send = kafkaProducer.send(producerRecord, null);

        while (!send.isDone()){
            Thread.sleep(1000);
        }

        try {
            send.get();
            assertThat(Boolean.TRUE, is(Boolean.TRUE));
        } catch (Exception e){
            assertThat(Boolean.TRUE, is(Boolean.FALSE));
        }


    }


}
