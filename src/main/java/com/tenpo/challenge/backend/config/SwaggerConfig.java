package com.tenpo.challenge.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                              .title("Tenpo Challenge API")
                              .version("1.0")
                              .description("Documentation for the services provided by the Tenpo Challenge API")
                );
    }
}
