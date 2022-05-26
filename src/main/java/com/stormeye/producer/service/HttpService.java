package com.stormeye.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

/**
 * Simple service to connect to a single event emitter
 * over HTTP
 */
@Service
class HttpService {

    private static final Logger log = LoggerFactory.getLogger(HttpService.class.getName());

    private HttpClient getClient(){
        return HttpClient.newHttpClient();
    }

    private HttpRequest getRequest(final URI emitter) {
        return HttpRequest.newBuilder(emitter).GET().build();
    }

    public Stream<String> emitterStream(final URI emitter) throws IOException, InterruptedException {
        return this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).body();
    }

    public boolean isValid(final URI emitter) {

        try {

            final HttpHeaders headers = this.getClient().send(this.getRequest(emitter), HttpResponse.BodyHandlers.ofLines()).headers();

            return headers != null;

        } catch (Exception e) {

            log.error("Emitter [{}] connection error", emitter);
            log.error(e.getMessage());

            return false;
        }


    }

}
