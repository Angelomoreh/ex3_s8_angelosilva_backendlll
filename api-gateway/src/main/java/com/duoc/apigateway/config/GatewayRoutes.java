package com.duoc.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Bean
    RouteLocator bancoRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("bff-web", route -> route
                        .path("/api/web/**")
                        .uri("lb://BFF-WEB"))
                .route("bff-mobile", route -> route
                        .path("/api/mobile/**")
                        .uri("lb://BFF-MOBILE"))
                .route("bff-atm", route -> route
                        .path("/api/atm/**")
                        .uri("lb://BFF-ATM"))
                .build();
    }
}
