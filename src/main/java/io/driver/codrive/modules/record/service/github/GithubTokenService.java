package io.driver.codrive.modules.record.service.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.global.auth.JwtProvider;
import io.driver.codrive.global.token.GithubToken;
import io.driver.codrive.global.token.GithubTokenRepository;
import io.driver.codrive.modules.auth.model.dto.GithubCodeDto;
import io.driver.codrive.modules.record.model.dto.GithubAccessTokenDto;
import io.driver.codrive.modules.record.model.dto.GithubRefreshDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubTokenService {
	private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
	private static final String GITHUB_TOKEN_VALIDATE_URL = "https://api.github.com/applications/%s/tokens";
	private static final String GITHUB_ACCESS_TOKEN_PREFIX = "access_token=";
	private static final String GITHUB_REFRESH_TOKEN_PREFIX = "refresh_token=";

	private final WebClient webClient;
	private final GithubTokenRepository githubTokenRepository;
	private final JwtProvider jwtProvider;

	@Value("${github.client_id}")
	private String clientId;

	@Value("${github.client_secret}")
	private String clientSecret;

	public String getGithubTokenResponse(String code) {
		try {
			return webClient.post()
				.uri(GITHUB_TOKEN_URL)
				.bodyValue(GithubCodeDto.createRequest(clientId, clientSecret, code))
				.retrieve()
				.toEntity(String.class)
				.block()
				.getBody();
		} catch (WebClientResponseException | NullPointerException e) {
			log.error("Invalid Code {} : {}", code, e.getMessage());
			throw new UnauthorizedApplicationException("유효하지 않은 코드입니다.");
		}
	}

	public String extractGithubAccessToken(String response) {
		return extractGithubToken(GITHUB_ACCESS_TOKEN_PREFIX, response);
	}

	public String extractGithubRefreshToken(String response) {
		return extractGithubToken(GITHUB_REFRESH_TOKEN_PREFIX, response);
	}

	public String extractGithubToken(String prefix, String response) {
		String suffix = "&";
		int startIndex = response.indexOf(prefix) + prefix.length();
        int endIndex = response.indexOf(suffix, startIndex);
        return response.substring(startIndex, endIndex);
	}

	public void saveGithubToken(Long userId, String accessToken, String response) {
		GithubToken token = new GithubToken(userId, accessToken, null);
		githubTokenRepository.save(token);
	}

	public GithubToken getGithubTokenByUserId(Long userId) {
		return githubTokenRepository.findById(String.valueOf(userId)).orElseThrow(
			() -> new NotFoundApplicationException("Github Token")
		);
	}

	public String getGithubAccessToken(Long userId) {
		return getGithubTokenByUserId(userId).getAccessToken();
	}

	private boolean validateGithubToken(String accessToken) {
		try {
			int statusCode = webClient.post()
				.uri(String.format(GITHUB_TOKEN_VALIDATE_URL, clientId))
				.bodyValue(GithubAccessTokenDto.of(accessToken))
				.retrieve()
				.toEntity(HttpStatusCode.class)
				.block()
				.getStatusCode().value();
			return statusCode == 200;
		} catch (WebClientResponseException e) {
			return false;
		}
	}

	private String getRefreshGithubTokenResponse(String refreshToken) {
		try {
			return webClient.post()
				.uri(GITHUB_TOKEN_URL)
				.bodyValue(GithubRefreshDto.of(clientId, clientSecret, refreshToken))
				.retrieve()
				.toEntity(String.class)
				.block()
				.getBody();
		} catch (Exception e) {
			log.error("GitHub Refresh Error {} : {}", refreshToken, e.getMessage());
			throw new InternalServerErrorApplicationException("GitHub 토큰 갱신에 실패했습니다.");
		}
	}

	private String refreshGithubToken(Long userId, String refreshToken) {
		String refreshResponse = getRefreshGithubTokenResponse(refreshToken);
		log.info("refreshResponse : {}", refreshResponse);
		String newAccessToken = extractGithubAccessToken(refreshResponse);
		String newRefreshToken = extractGithubRefreshToken(refreshResponse);
		githubTokenRepository.save(new GithubToken(userId, newAccessToken, newRefreshToken));
		return newAccessToken;
	}

}
