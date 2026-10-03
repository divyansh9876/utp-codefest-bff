package com.utpcodefest.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI bffOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("UTP CodeFest BFF API")
						.version("v1")
						.description("Spring Boot Backend-for-Frontend for the UTP CodeFest workshop. "
								+ "The Next.js frontend calls these endpoints through its /api proxy."))
				// Registered now so Phase 3's protected endpoints get an "Authorize" button.
				.components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));
	}
}
