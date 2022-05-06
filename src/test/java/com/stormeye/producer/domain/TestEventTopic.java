package com.stormeye.producer.domain;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.producer.config.Topics;
import com.stormeye.producer.exceptions.EventException;

public class TestEventTopic {

    @Test
    void TestValidTopic(){
        final String event = "data:{\"FinalitySignature\":{\"block_hash\":\"4d03e2a7701f1da60bc48c9d091667eb89eb8f3ab41a153ad441e100a2602eb2\",\"era_id\":4689,\"signature\":\"01870119d75d61349190e5a5c6e1ee047318f129978d09d77005426a51642f11c95e11aff1c4725ed81ed94c9fad1d4801a84ca1a19bc748295c9ffe99cf925703\",\"public_key\":\"0129a40cf2738f2b74606ea25a3ddf8cd88f5f729a1018b929795adab19d44dc90\"}}";
        assert(new EventTopic(event, new ObjectMapper()).getTopic().equals(Topics.FinalitySignature));
    }

    @Test
    void TestInValidTopic(){
        Exception exception = assertThrows(EventException.class, () -> {
            final String event = "data:{\"UnknownTopic\":{\"block_hash\":\"4d03e2a7701f1da60bc48c9d091667eb89eb8f3ab41a153ad441e100a2602eb2\",\"era_id\":4689,\"signature\":\"01870119d75d61349190e5a5c6e1ee047318f129978d09d77005426a51642f11c95e11aff1c4725ed81ed94c9fad1d4801a84ca1a19bc748295c9ffe99cf925703\",\"public_key\":\"0129a40cf2738f2b74606ea25a3ddf8cd88f5f729a1018b929795adab19d44dc90\"}}";
            new EventTopic(event, new ObjectMapper()).getTopic();
        });
    }

    @Test
    void TestInValidStructure(){
        Exception exception = assertThrows(EventException.class, () -> {
            final String event = "event:{\"FinalitySignature\":{\"block_hash\":\"4d03e2a7701f1da60bc48c9d091667eb89eb8f3ab41a153ad441e100a2602eb2\",\"era_id\":4689,\"signature\":\"01870119d75d61349190e5a5c6e1ee047318f129978d09d77005426a51642f11c95e11aff1c4725ed81ed94c9fad1d4801a84ca1a19bc748295c9ffe99cf925703\",\"public_key\":\"0129a40cf2738f2b74606ea25a3ddf8cd88f5f729a1018b929795adab19d44dc90\"}}";
            new EventTopic(event, new ObjectMapper()).getTopic();
        });
    }


}
