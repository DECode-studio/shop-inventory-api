package com.geli.shopinventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopInventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop Warehouse Inventory API")
                        .description("RESTful API for shop warehouse inventory management system with variant tracking, pricing, and anti-overselling stock control.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GELI Assessment Candidate")
                                .email("candidate@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

