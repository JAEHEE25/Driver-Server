package io.driver.codrive.modules.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.global.jwt.JwtProvider;
import io.driver.codrive.modules.auth.domain.RefreshToken;
import io.driver.codrive.modules.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	private final JwtProvider jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	public String generateAccessToken(Long userId) {
		return jwtProvider.generateAccessToken(userId);
	}

	public String generateRefreshToken() {
		return jwtProvider.createRefreshToken();
	}

	@Transactional
	public void saveRefreshToken(String accessToken, String refreshToken, Long userId) {
		RefreshToken token = new RefreshToken(accessToken, refreshToken, userId);
		refreshTokenRepository.save(token);
	}

	public String generateNewAccessToken(String requestAccessToken, String requestRefreshToken) {
		RefreshToken refreshToken = getRefreshToken(requestAccessToken);

		log.info("requestRefreshToken: {}", requestRefreshToken);
		log.info("refreshToken.getRefreshToken(): {}", refreshToken.getRefreshToken());

		if (requestRefreshToken.equals(refreshToken.getRefreshToken())) {
			return generateAccessToken(refreshToken.getUserId());
		} else {
			refreshTokenRepository.delete(refreshToken);
			throw new UnauthorizedApplicationException("Refresh Token이 유효하지 않습니다.");
		}
	}

	private RefreshToken getRefreshToken(String accessToken) {
		log.info("accessToken: {}", accessToken);
		return refreshTokenRepository.findById(accessToken).orElseThrow(
			() -> new UnauthorizedApplicationException("Refresh Token이 만료되었습니다.")
		);
	}
}
