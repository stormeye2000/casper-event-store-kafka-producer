package com.stormeye.producer.service;

import static org.junit.jupiter.api.Assertions.*;

import com.casper.sdk.model.event.EventType;
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

import java.io.IOException;
import java.net.URI;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(classes = {EmitterService.class, ServiceProperties.class, AppConfig.class})
@EnableConfigurationProperties(value = {ServiceProperties.class})
@EnableAutoConfiguration
public class TestHttpService {

    public static MockWebServer mockWebServer;

    @Autowired
    private EmitterService service;

    @Autowired
    private RetryTemplate retryTemplate;

    private static String EVENT_STREAM;

    static {
        try {
            //noinspection resource
            EVENT_STREAM = new String(
                    (TestHttpService.class.getClassLoader().
                            getResourceAsStream("events.stream"))
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

        assertThrows(Exception.class, () -> retryTemplate.execute(ctx -> {
            URI uri = URI.create("http://localhost:9999");
            service.connect(uri);
            service.emitterStream(uri, EventType.MAIN);
            return null;
        }));
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
    void testSimpleHttp() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"id\": 1}")
                .setResponseCode(200));

        service.emitterStream(URI.create(String.format("http://localhost:%s", mockWebServer.getPort())), EventType.MAIN).forEach(
                event -> assertEquals(1, event.getId())
        );
    }

    @Test
    void testWithEvents() {

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody(EVENT_STREAM)
                .setResponseCode(200));

        service.emitterStream(URI.create(String.format("http://localhost:%s", mockWebServer.getPort())), EventType.MAIN).forEach(
           event -> assertNotNull(event.getEventType())
        );
    }

}
