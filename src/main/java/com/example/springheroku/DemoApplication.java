package com.example.springheroku;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        var env = event.getApplicationContext().getEnvironment();

        logApplicationStartup(env);
        logApplicationConfig(env);
    }

    private static void logApplicationStartup(Environment env) {
        var protocol    = env.getProperty("server.ssl.key-store") != null ? "https" : "http";
        var serverPort  = env.getProperty("server.port");
        var contextPath = env.getProperty("server.servlet.context-path", "");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        var hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                     "Application '{}' is running! Access URLs:\n\t" +
                     "Local: \t\t{}://localhost:{}{}\n\t" +
                     "External: \t{}://{}:{}{}\n\t" +
                     "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

    private static void logApplicationConfig(Environment env) {
        log.info("====== Environment and configuration ======");
        ((AbstractEnvironment) env)
            .getPropertySources()
            .stream()
            .filter(ps -> ps instanceof EnumerablePropertySource)
            .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
            .flatMap(Arrays::stream)
            .distinct()
            .filter(DemoApplication::exclude)
            .sorted()
            .forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
        log.info("===========================================");
    }

    private static boolean exclude(String prop) {
        return !(prop.contains("credentials") ||
                     prop.contains("password") ||
                     prop.contains("secret") ||
                     prop.contains("token"));
    }
}
