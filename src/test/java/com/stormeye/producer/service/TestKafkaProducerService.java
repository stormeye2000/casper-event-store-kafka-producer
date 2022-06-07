package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.exceptions.EmitterStoppedException;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.kafka.sender.SenderOptions;


@SpringBootTest(classes = {EmitterService.class, TopicsService.class, ServiceProperties.class, KafkaProducerService.class, ProducerService.class, AppConfig.class})
@EnableConfigurationProperties(value = ServiceProperties.class)
@EnableAutoConfiguration
@EmbeddedKafka(topics = TestKafkaProducerService.REACTIVE_INT_KEY_TOPIC, partitions = 1)
public class TestKafkaProducerService {

    static final String REACTIVE_INT_KEY_TOPIC = "FinalitySignature";

    public static MockWebServer mockWebServer;

    @Autowired
    private EmitterService emitterService;

    @Autowired
    private TopicsService topics;


    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);


    private ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate;


    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getEmbeddedKafka().getBrokersAsString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "org.apache.kafka.clients.producer.internals.DefaultPartitioner");
        return props;
    }



    private static String EVENT_STREAM;

    static {
        try {
            EVENT_STREAM = new String(
                    (Objects.requireNonNull(TestHttpService.class.getClassLoader().
                            getResourceAsStream
                                    ("events.stream")))
                            .readAllBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeEach
    void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Test
    void TestEmitterEnded() {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        Exception ex = new Exception();

        AsynchTester tester = new AsynchTester(new ProducerThread(reactiveKafkaProducerTemplate, emitterService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));

        try{
            tester.start();
            tester.test();
        } catch (Exception e){
            ex = e;
        }

        assertNotNull(ex);
        assertTrue(ex instanceof EmitterStoppedException);

    }

    static class AsynchTester{
        private Thread thread;
        private EmitterStoppedException exc;

        public AsynchTester(final Runnable runnable){
            thread = new Thread(() -> {
                try{
                    runnable.run();
                }catch(EmitterStoppedException e){
                    exc = e;
                }
            });
        }

        public void start(){
            thread.start();
        }

        public void test() throws InterruptedException{
            thread.join();
            if (exc != null)
                throw exc;
        }
    }


    @Test
    @Disabled
    void TestProducer() throws InterruptedException {

        this.reactiveKafkaProducerTemplate = new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerConfigs()),
                new MessagingMessageConverter());

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(EVENT_STREAM)
                .setResponseCode(200));

        CountDownLatch latch = new CountDownLatch(3);
        List<ProducerThread> tasks = new ArrayList<>();
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, emitterService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, emitterService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, emitterService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));

        Executor executor = Executors.newFixedThreadPool(tasks.size());

        for (ProducerThread thread: tasks){
            executor.execute(thread);
        }
        latch.await(1, TimeUnit.MINUTES);

        for(final ProducerThread thread: tasks){
            if( ! thread.isAlive()){
                assertTrue(false);
            }
        }
       assertTrue(true);

    }


}
