package io.driver.codrive.modules.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@Configuration
public class JwtConfig {
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access_token.expiration_ms}")
	private Long expirationMills;  //24시간
}
