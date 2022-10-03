package com.stormeye.producer.service.producer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.stormeye.producer.config.BrokerState;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.util.Properties;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableAutoConfiguration
@Disabled
public class TestLargeEvent {

    public MockWebServer mockWebServer;
    @Autowired
    private ProducerService producerService;
    @Autowired
    private BrokerState brokerState;
    @Autowired
    private MongoOperations mongoOperations;

    @AfterEach
    void teardown() {
        ((MongoTemplate) mongoOperations).getDb().drop();
    }

    @BeforeEach
    void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Test
    @Disabled
    void testStandardEventWithOutService() throws Exception {

        File file = ResourceUtils.getFile("classpath:standard.event");
        String event = new String(Files.readAllBytes(file.toPath()));

        Event<?> step = buildEvent(event);
        sendEvent(step);

    }

    @Test
    @Disabled
    void testStandardEventWithService() throws Exception {

        final URI emitter = URI.create(String.format("http://localhost:%s", mockWebServer.getPort()));
        File file = ResourceUtils.getFile("classpath:standard.event");
        String event = new String(Files.readAllBytes(file.toPath()));

        Event<?> step = buildEvent(event);
        producerService.sendEvent(emitter, step);

    }


    @Test
    void testLargeEventWithService() throws Exception {

        assertThat(brokerState.isAvailable(), is(true));

        final URI emitter = URI.create(String.format("http://localhost:%s", mockWebServer.getPort()));

        File file = ResourceUtils.getFile("classpath:step-large.event");
        String event = new String(Files.readAllBytes(file.toPath()));
        Event<?> step = buildEvent(event);

        producerService.sendEvent(emitter, step);

    }


    @Test
    @Disabled
    void testLargeEventWithoutService() throws Exception {

        assertThat(brokerState.isAvailable(), is(true));

        File file = ResourceUtils.getFile("classpath:step-large.event");
        String event = new String(Files.readAllBytes(file.toPath()));

        Event<?> step = buildEvent(event);

        sendEvent(step);

    }


    private void sendEvent(final Event<?> event) {
        String topic = "main";

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("buffer.memory", 33554432);
        props.put("max.request.size", 33554432);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, event.toString());
        TestCallback callback = new TestCallback();

        producer.send(producerRecord, callback);

    }



    private static class TestCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                System.out.println("Error while producing message to topic :" + recordMetadata);
                e.printStackTrace();
            } else {
                String message = String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                System.out.println(message);
            }
        }
    }


    private static Event<String> buildEvent(final String event) throws Exception {

        Class<?> eventBuildClass = Class.forName("com.casper.sdk.service.impl.event.EventBuilder");
        Constructor<?> ctor = eventBuildClass.getDeclaredConstructor(EventType.class, EventTarget.class, String.class);
        ctor.setAccessible(true);
        Object eventBuilder = ctor.newInstance(EventType.MAIN, EventTarget.RAW, event);

        Method processLine = eventBuildClass.getDeclaredMethod("processLine", String.class);
        processLine.setAccessible(true);

        processLine.invoke(eventBuilder, event);

        Method buildEvent = eventBuildClass.getDeclaredMethod("buildEvent");
        buildEvent.setAccessible(true);

        //noinspection unchecked
        return (Event<String>) buildEvent.invoke(eventBuilder);
    }

}