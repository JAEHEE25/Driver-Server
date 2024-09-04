package io.driver.codrive.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().addServersItem(new Server().url("/"))
			.components(new Components()
				.addSecuritySchemes("Authorization", new SecurityScheme().type(SecurityScheme.Type.APIKEY)
					.in(SecurityScheme.In.HEADER)
					.name("Authorization"))).addSecurityItem(new SecurityRequirement().addList("Authorization"))
			.info(getInfo());
	}

	private Info getInfo() {
		return new Info()
			.title("Codrive API")
			.description("Codrive API 문서입니다.")
			.version("1.0")
			.termsOfService("https://github.com/Co-Drive/Driver-Server")
			.contact(new Contact()
				.name("codrive-dev")
				.url("https://github.com/orgs/Co-Drive/repositories")
				.email("y2hjjh@naver.com")
			);
	}

}
