package com.stormeye.producer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(classes = {TestServiceProperties.class})
@EnableConfigurationProperties(value = ServiceProperties.class)
@EnableAutoConfiguration
public class TestServiceProperties {

    @Autowired
    private ServiceProperties properties;

    @Test
    void testDefaultProperties() throws URISyntaxException {
        assertEquals(new URI("http://65.21.235.219:9999"), properties.getEmitters().get(0));
    }

    @Test
    void testTopics() {

        assertEquals("sigs", properties.getTopics().get(0).getTopic());
        assertEquals(5, properties.getTopics().get(1).getReplicas());
        assertEquals(3, properties.getTopics().get(2).getPartitions());

    }

}
