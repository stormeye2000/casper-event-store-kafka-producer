package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import com.stormeye.producer.config.AppConfig;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.service.emitter.EmitterService;
import com.stormeye.producer.service.topics.TopicsService;

import java.io.IOException;
import java.net.URI;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(classes = {EmitterService.class, TopicsService.class, ServiceProperties.class, AppConfig.class})
@EnableConfigurationProperties(value = {ServiceProperties.class})
@EnableAutoConfiguration
public class TestHttpService {

    public static MockWebServer mockWebServer;

    @Autowired
    private EmitterService service;

    @Autowired
    private TopicsService topics;

    @Autowired
    private RetryTemplate retryTemplate;

    private static String EVENT_STREAM;

    static {
        try {
            EVENT_STREAM = new String(
                    (TestHttpService.class.getClassLoader().
                            getResourceAsStream
                                    ("events.stream"))
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
    void testInvalidConnection() {

        Assertions.assertThrows(Exception.class, () -> {
            retryTemplate.execute(ctx -> {
                service.connect(URI.create("http://localhost:9999"));
                return null;
            });
        });

    }

    @Test
    void testValidConnection() {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        RetryContext context = retryTemplate.execute(ctx -> {
            try {
                service.connect((URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ctx;
        });

        assertNull(context.getLastThrowable());
    }


    @Test
    void testSimpleHttp() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"id\": 1}")
                .setResponseCode(200));

        service.emitterStream(URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))).forEach(
                event -> assertEquals("{\"id\": 1}", event)
        );

    }

    @Test
    void testWithEvents() throws IOException, InterruptedException {

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody(EVENT_STREAM)
                .setResponseCode(200));

        service.emitterStream(URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))).forEach(
           event ->     {
               assertTrue(topics.hasTopic(event));
           }
        );
    }

}
