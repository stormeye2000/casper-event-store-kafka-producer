package com.stormeye.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.stormeye.producer.config.KafkaProducer;
import com.stormeye.producer.config.Topics;
import com.stormeye.producer.service.ReceiverService;

import java.net.URISyntaxException;
import java.util.List;
import reactor.kafka.sender.KafkaSender;

@SpringBootApplication
public class ProducerApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(ProducerApplication.class, args);

		KafkaSender<Integer, String> kafkaProducer = new KafkaProducer().getSender();

		new ReceiverService("http://65.21.235.219:9999/events/main", List.of(Topics.DeployProcessed, Topics.BlockAdded), kafkaProducer).start();
		new ReceiverService("http://65.21.235.219:9999/events/deploys", List.of(Topics.DeployAccepted), kafkaProducer).start();
		new ReceiverService("http://65.21.235.219:9999/events/sigs", List.of(Topics.FinalitySignature), kafkaProducer).start();


	}

}
