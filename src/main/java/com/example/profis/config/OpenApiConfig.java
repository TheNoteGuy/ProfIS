package com.example.profis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        try {
            ClassPathResource resource = new ClassPathResource("openapi/profis-api.yml");
            InputStream inputStream = resource.getInputStream();
            String yamlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return new OpenAPIV3Parser().readContents(yamlContent).getOpenAPI();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load OpenAPI specification", e);
        }
    }
}