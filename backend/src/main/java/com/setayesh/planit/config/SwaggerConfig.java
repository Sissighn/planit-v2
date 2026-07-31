package com.setayesh.planit.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for PlanIt REST API.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI planItOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PlanIt Task Management API")
                        .description(
                                "REST API for managing tasks, priorities, and archives — same logic as CLI version.")
                        .version("1.0.0"));
    }
}
