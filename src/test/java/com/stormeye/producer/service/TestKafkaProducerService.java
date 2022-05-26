package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import com.stormeye.producer.config.ServiceProperties;

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


@SpringBootTest(classes = {HttpService.class, TopicsService.class, ServiceProperties.class, KafkaProducerService.class, ProducerService.class})
@EnableConfigurationProperties(value = ServiceProperties.class)
@EnableAutoConfiguration
@EmbeddedKafka(topics = TestKafkaProducerService.REACTIVE_INT_KEY_TOPIC, partitions = 1)
public class TestKafkaProducerService {

//    private static final Logger log = LoggerFactory.getLogger(TestKafkaProducerService.class.getName());
    static final String REACTIVE_INT_KEY_TOPIC = "FinalitySignature";

    public static MockWebServer mockWebServer;

    @Autowired
    private HttpService httpService;

    @Autowired
    private TopicsService topics;


    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);


    private ReactiveKafkaProducerTemplate<Integer, String> reactiveKafkaProducerTemplate;


    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getEmbeddedKafka().getBrokersAsString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "reactive_consumer_group");
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
    void TestProducer() throws InterruptedException {

        this.reactiveKafkaProducerTemplate = new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerConfigs()),
                new MessagingMessageConverter());

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(EVENT_STREAM)
                .setResponseCode(200));

        CountDownLatch latch = new CountDownLatch(3);
        List<ProducerThread> tasks = new ArrayList<>();
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, httpService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, httpService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));
        tasks.add(new ProducerThread(reactiveKafkaProducerTemplate, httpService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));

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
