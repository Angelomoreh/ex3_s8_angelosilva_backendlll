package com.duoc.bffatm.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Qualifier("cuentasRestClient")
    RestClient cuentasRestClient(
            @LoadBalanced RestClient.Builder builder,
            @Value("${backend.cuentas.url}") String baseUrl
    ) {
        return builder.clone().baseUrl(baseUrl).build();
    }
}
