package io.driver.codrive.modules.auth.service.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.driver.codrive.modules.global.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
	private final JwtConfig jwtConfig;
	private final SecretKey secretKey;

	public JwtProvider(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtConfig.getSecretKey()));
	}

	public String generateAccessToken(Long userId) {
		return createAccessToken(userId, jwtConfig.getExpirationMills());
	}

	private String createAccessToken(Long userId, Long expirationTime) {
		return Jwts.builder()
			.subject(String.valueOf(userId))
			.expiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(secretKey)
			.compact();
	}

}
