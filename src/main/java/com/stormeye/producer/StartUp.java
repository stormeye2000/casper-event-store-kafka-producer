package com.stormeye.producer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.stormeye.producer.service.producer.ProducerService;

/**
 * Starts the application via the ProducerService
 */
@Component
class StartUp implements ApplicationRunner {

    private final ProducerService service;

    private StartUp(final ProducerService service) {
        this.service = service;
    }

    @Override
    public void run(final ApplicationArguments args) {
        service.startEventConsumers();
    }



}
