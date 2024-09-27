package io.driver.codrive.modules.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.global.jwt.JwtProvider;
import io.driver.codrive.global.token.AuthToken;
import io.driver.codrive.global.token.AuthTokenRepository;
import io.driver.codrive.global.token.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppTokenService {
	private final JwtProvider jwtProvider;
	private final AuthTokenRepository authTokenRepository;

	public String generateAccessToken(Long userId) {
		return jwtProvider.generateAccessToken(userId);
	}

	public String generateRefreshToken() {
		return jwtProvider.createRefreshToken();
	}

	@Transactional
	public void saveAuthToken(String accessToken, String refreshToken, Long userId) {
		AuthToken token = new AuthToken(userId, accessToken, refreshToken, TokenType.APP.name());
		authTokenRepository.save(token);
	}

	@Transactional
	public String generateNewAccessToken(String requestAccessToken, String requestRefreshToken) {
		AuthToken authToken = getAuthToken(requestAccessToken);

		if (requestRefreshToken.equals(authToken.getRefreshToken())) {
			return generateAccessToken(authToken.getUserId());
		} else {
			authTokenRepository.delete(authToken);
			throw new UnauthorizedApplicationException("Refresh Token이 유효하지 않습니다.");
		}
	}

	private AuthToken getAuthToken(String accessToken) {
		log.info("accessToken: {}", accessToken);
		String userId = jwtProvider.getClaims(accessToken).getSubject();

		AuthToken authToken = authTokenRepository.findById(userId).orElseThrow(
			() -> new UnauthorizedApplicationException("Refresh Token이 만료되었습니다.")
		);

		if (authToken.getTokenType().equals(TokenType.APP.name())) {
			return authToken;
		} else {
			throw new UnauthorizedApplicationException("Refresh Token이 유효하지 않습니다.");
		}
	}
}
