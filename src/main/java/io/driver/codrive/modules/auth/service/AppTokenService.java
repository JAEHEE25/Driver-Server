package io.driver.codrive.modules.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.global.jwt.JwtProvider;
import io.driver.codrive.global.token.AppToken;
import io.driver.codrive.global.token.AppTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppTokenService {
	private final JwtProvider jwtProvider;
	private final AppTokenRepository appTokenRepository;

	public String generateAccessToken(Long userId) {
		return jwtProvider.generateAccessToken(userId);
	}

	public String generateRefreshToken() {
		return jwtProvider.createRefreshToken();
	}

	@Transactional
	public void saveAppToken(String accessToken, String refreshToken, Long userId) {
		AppToken token = new AppToken(userId, accessToken, refreshToken);
		appTokenRepository.save(token);
	}

	@Transactional
	public String generateNewAccessToken(String requestAccessToken, String requestRefreshToken) {
		AppToken appToken = getAppTokenByAccessToken(requestAccessToken);

		if (requestRefreshToken.equals(appToken.getRefreshToken())) {
			return generateAccessToken(appToken.getUserId());
		} else {
			appTokenRepository.delete(appToken);
			throw new UnauthorizedApplicationException("Refresh Token이 유효하지 않습니다.");
		}
	}

	private AppToken getAppTokenByAccessToken(String accessToken) {
		String userId = jwtProvider.getClaims(accessToken).getSubject();
		log.info("userId : {}", userId);
		return appTokenRepository.findById(userId).orElseThrow(
			() -> new UnauthorizedApplicationException("Refresh Token이 만료되었습니다.")
		);
	}
}
