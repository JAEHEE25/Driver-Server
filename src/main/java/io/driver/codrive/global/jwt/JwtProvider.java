package io.driver.codrive.global.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.driver.codrive.global.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {
	private final JwtConfig jwtConfig;
	private final SecretKey secretKey;

	public JwtProvider(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtConfig.getSecretKey()));
	}

	public String generateAccessToken(Long userId) {
		return createAccessToken(userId, jwtConfig.getAccessTokenExpirationMills());
	}

	private String createAccessToken(Long userId, Long expirationTime) {
		return Jwts.builder()
			.subject(String.valueOf(userId))
			.expiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken() {
		return Jwts.builder()
			.expiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpirationMills()))
			.signWith(secretKey)
			.compact();
	}

	public Claims getClaims(String accessToken) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(accessToken)
				.getPayload();
		} catch (Exception e) {
            log.error("Invalid JWT token", e);
            return null;
		}
	}
}
