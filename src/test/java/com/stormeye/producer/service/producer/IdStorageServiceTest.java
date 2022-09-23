package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class IdStorageServiceTest {

    @Autowired
    private IdStorageService idStorageService;

    @Autowired
    private MongoOperations mongoOperations;

    @BeforeEach
    void setUp() {


    }

    @Test
    void createService() {
        assertThat(idStorageService, is(notNullValue()));
        assertThat(((MongoTemplate)mongoOperations).getDb().getName(), is("test-casper-producer"));
    }


    @Test
    void getId() {

        idStorageService.getCurrentId(EventType.MAIN);
    }
}