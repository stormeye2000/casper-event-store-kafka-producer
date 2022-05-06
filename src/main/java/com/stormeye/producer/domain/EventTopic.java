package com.stormeye.producer.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.producer.config.Topics;
import com.stormeye.producer.exceptions.EventException;

public class EventTopic {

    private final String event;
    private final ObjectMapper mapper;

    public EventTopic(final String event, final ObjectMapper mapper) {
        this.event = event;
        this.mapper = mapper;
    }

    public Topics getTopic(){
        return extractTopicFromEvent();
    }

    private Topics extractTopicFromEvent() {

        try{
            return Topics.valueOf(mapper.readValue(event.replace("data:", ""), JsonNode.class)
                    .fieldNames().next());

        } catch (Exception e){
            throw new EventException(e.getMessage());
        }

    }

}
