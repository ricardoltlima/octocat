package com.brand.octocat.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    @Test
    void githubRestTemplate_shouldCreateRestTemplate() {
        RestTemplateConfig config = new RestTemplateConfig("https://api.github.com");
        RestTemplate restTemplate = config.githubRestTemplate(new RestTemplateBuilder());

        assertThat(restTemplate).isNotNull();
    }
}
