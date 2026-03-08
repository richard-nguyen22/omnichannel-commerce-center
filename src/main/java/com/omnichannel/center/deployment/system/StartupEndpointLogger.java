package com.omnichannel.center.deployment.system;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;
import java.util.TreeSet;

@Component
public class StartupEndpointLogger {
    private final RequestMappingHandlerMapping handlerMapping;
    private final Environment environment;

    public StartupEndpointLogger(RequestMappingHandlerMapping handlerMapping, Environment environment) {
        this.handlerMapping = handlerMapping;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logEndpoints() {
        String port = environment.getProperty("server.port", "8888");
        String baseUrl = "http://localhost:" + port;

//        Set<String> endpointPaths = new TreeSet<>();
//        handlerMapping.getHandlerMethods().forEach((info, method) -> {
//            if (info.getPathPatternsCondition() != null) {
//                info.getPathPatternsCondition().getPatterns()
//                        .forEach(pattern -> endpointPaths.add(pattern.getPatternString()));
//            }
//        });

        System.out.println("========================================");
        System.out.println("CommerceCenterApplication started");
        System.out.println("Base URL: " + baseUrl);
//        System.out.println("Mapped endpoint paths:");
//        for (String path : endpointPaths) {
//            System.out.println("- " + path);
//        }
        System.out.println("========================================");
    }
}
