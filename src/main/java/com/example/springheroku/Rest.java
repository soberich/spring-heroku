package com.example.springheroku;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class Rest {

    public static final String URI_STR = "https://quoters.apps.pcfone.io/api/random";

    public static final java.net.URI URI = java.net.URI.create(URI_STR);

    private final RestTemplate restTemplate;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @RequestMapping("/")
    public String triggerManually() {
        var quote =
            Optional.ofNullable(restTemplate.getForObject(URI, Quote.class))
                .toString();
        log.info("Response random quote - {}", quote);
        return quote;
    }

    @Data
    static class Value {
        Integer id;
        String  quote;
    }

    @Data
    static class Quote {
        String type;
        Value  value;
    }
}
