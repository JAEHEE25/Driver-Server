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

	@Value("${jwt.accessToken.expirationMs}")
	private Long expirationMills;  //24시간
}
