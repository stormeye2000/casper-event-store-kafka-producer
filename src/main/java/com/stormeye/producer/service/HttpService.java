package com.stormeye.producer.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Simple service to connect to a single event emitter
 * over HTTP
 */
public class HttpService {

    final URI emitter;

    public HttpService(final URI emitter) {
        this.emitter = emitter;
    }

    public HttpClient getClient(){
        return HttpClient.newHttpClient();
    }

   public HttpRequest getRequest() {
       return HttpRequest.newBuilder(emitter).GET().build();
    }

}
