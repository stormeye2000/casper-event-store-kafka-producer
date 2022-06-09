package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.stormeye.producer.service.emitter.EmitterService;
import com.stormeye.producer.service.producer.KafkaProducerService;
import com.stormeye.producer.service.producer.ProducerCallable;
import com.stormeye.producer.service.producer.ProducerService;
import com.stormeye.producer.service.topics.TopicsService;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.kafka.sender.SenderOptions;


@SpringBootTest(classes = {EmitterService.class, TopicsService.class, ServiceProperties.class, KafkaProducerService.class, ProducerService.class, AppConfig.class})
@EnableConfigurationProperties(value = ServiceProperties.class)
@EnableAutoConfiguration
@EmbeddedKafka(topics = TestKafkaProducerService.REACTIVE_INT_KEY_TOPIC, partitions = 1)
public class TestKafkaProducerService {

    static final String REACTIVE_INT_KEY_TOPIC = "FinalitySignature";

    public MockWebServer mockWebServer;

    @Autowired
    private EmitterService emitterService;

    @Autowired
    private TopicsService topics;


    @ClassRule
    public static final EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);


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
    void testEmitterEnded() {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));
                //no body so emitter will stop

        final ExecutorService executor= Executors.newFixedThreadPool(1);
        final Future<Object> future = executor.submit(new ProducerCallable(reactiveKafkaProducerTemplate, emitterService, topics, URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));

        try {
            future.get();
        } catch (ExecutionException e){
            assertEquals(EmitterStoppedException.class, e.getCause().getClass());
        } catch (Exception e) {
            fail();
        }

        executor.shutdown();
    }

    @Test
    @Disabled
    void testProducer() {

        this.reactiveKafkaProducerTemplate = new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerConfigs()),
                new MessagingMessageConverter());

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(EVENT_STREAM)
                .setResponseCode(200));
    }
}
