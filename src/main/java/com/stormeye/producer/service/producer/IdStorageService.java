package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

/**
 * @author ian@meywood.com
 */
@Service
public class IdStorageService {

    private final MongoOperations mongoOperations;

    public IdStorageService(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


    public long getCurrentId(final EventType eventType) {
        return 0L;
    }

    public void setCurrentEvent(final EventType eventType, final long id) {

    }
}
