package com.stormeye.producer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.stormeye.producer.service.ProducerService;

/**
 * Starts the application via the ProducerService
 */
@Component
public class StartUp implements ApplicationRunner {

    final ProducerService service;

    public StartUp(final ProducerService service) {
        this.service = service;
    }

    @Override
    public void run(final ApplicationArguments args) {
        service.startEventConsumers();
    }
}
