package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * The service for saving and obtaining the current event IDs
 *
 * @author ian@meywood.com
 */
@Service
public class IdStorageService {

    private static final String EVENT_IDS = "eventIds";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String ID = "id";
    private final MongoOperations mongoOperations;

    public IdStorageService(@Autowired final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        createIndex();
    }

    public long getCurrentId(final String source, final EventType eventType) {
        var eventId = mongoOperations.findOne(createQuery(source, eventType), Document.class, EVENT_IDS);
        return eventId != null ? eventId.getLong(ID) : 0L;
    }

    public long getNextId(final String source, final EventType eventType) {
        long currentId = getCurrentId(source, eventType);
        // If the ID is zero use it otherwise increment it
        return currentId == 0 ? currentId : currentId + 1;
    }

    public void setCurrentEvent(final String source, final EventType eventType, final long id) {

        mongoOperations.upsert(
                createQuery(source, eventType),
                Update.update(SOURCE, source).set(TYPE, eventType.name()).set(ID, id),
                EVENT_IDS
        );
    }

    @NotNull
    private Query createQuery(String source, EventType eventType) {
        return Query.query(Criteria.where(SOURCE).is(source).and(TYPE).is(eventType.name()));
    }

    private void createIndex() {
        this.mongoOperations.getCollection(EVENT_IDS).createIndex(
                Indexes.compoundIndex(new BasicDBObject(SOURCE, 1), new BasicDBObject(TYPE, 1))
        );
    }
}
