package com.stormeye.producer.service.emitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

/**
 * Simple service to connect to a single event emitter
 * over HTTP
 * connect method will retry n times
 */
@Service
public class EmitterService {

    private final Logger log = LoggerFactory.getLogger(EmitterService.class.getName());

    private HttpClient getClient(){
        return HttpClient.newHttpClient();
    }

    private HttpRequest getRequest(final URI emitter) {
        return HttpRequest.newBuilder(emitter).GET().build();
    }

    public Stream<String> emitterStream(final URI emitter) throws IOException, InterruptedException {
        return this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).body();
    }

    public void connect(final URI emitter) throws IOException, InterruptedException {
       log.info("Attempting to connect to [{}]", emitter);

       this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).headers();
    }

}
