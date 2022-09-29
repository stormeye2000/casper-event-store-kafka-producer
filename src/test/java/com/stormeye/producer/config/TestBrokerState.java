package com.stormeye.producer.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {BrokerState.class, AppConfig.class, ServiceProperties.class})
public class TestBrokerState {

    @Autowired
    private BrokerState brokerState;

    @Test
    void testIsNotAvailable(){

        assertThat(brokerState, is(notNullValue()));

        assertThat(brokerState.isAvailable(), is(false));

    }





}
