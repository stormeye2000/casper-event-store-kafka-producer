package com.stormeye.producer.service.topics;

import org.apache.commons.text.CaseUtils;

public enum Topics {

    DEPLOY_PROCESSED, DEPLOY_EXPIRED, BLOCK_ADDED, DEPLOY_ACCEPTED, FINALITY_SIGNATURE, STEP, FAULT;

    public String toCamelCase(final Topics topic){
        return CaseUtils.toCamelCase(topic.toString(),true, '_');
    }


}
