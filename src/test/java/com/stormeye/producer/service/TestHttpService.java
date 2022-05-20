package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import com.stormeye.producer.config.ServiceProperties;

import java.io.IOException;
import java.net.URI;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(classes = {HttpService.class, TopicsService.class, ServiceProperties.class})
@EnableConfigurationProperties(value = ServiceProperties.class)
@EnableAutoConfiguration
public class TestHttpService {

    public static MockWebServer mockWebServer;

    @Autowired
    private HttpService service;

    @Autowired
    private TopicsService topics;

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
    void TestSimpleHttp() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"id\": 1}")
                .setResponseCode(200));

        service.emitterStream(URI.create(String.format("http://localhost:%s", mockWebServer.getPort()))).forEach(
                event -> assertEquals("{\"id\": 1}", event)
        );

    }

    @Test
    void TestWithEvents() throws IOException, InterruptedException {

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
