package com.brand.octocat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${github.base-url}")
    private final String baseUrl;

    public RestTemplateConfig(@Value("${github.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    public RestTemplate githubRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(baseUrl)
                .build();
    }
}

