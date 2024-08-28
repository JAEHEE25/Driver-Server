package io.driver.codrive.modules.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.driver.codrive.modules.auth.model.dto.GithubCodeDto;
import io.driver.codrive.modules.auth.model.request.GithubLoginRequest;
import io.driver.codrive.modules.auth.model.dto.GithubUserProfile;
import io.driver.codrive.modules.auth.model.request.RefreshTokenRequest;
import io.driver.codrive.modules.auth.model.response.AccessTokenResponse;
import io.driver.codrive.modules.auth.model.response.LoginResponse;
import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
	private static final String GITHUB_USER_PROFILE_URL = "https://api.github.com/user";
	private final LanguageService languageService;
	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final WebClient webClient = WebClient.create();

	@Value("${github.client_id}")
	private String clientId;

	@Value("${github.client_secret}")
	private String clientSecret;

	@Transactional
	public LoginResponse socialLogin(GithubLoginRequest request) {
		String githubAccessToken = extractGithubAccessToken(getGithubAccessTokenResponse(request.code()));
		GithubUserProfile userProfile = getUserProfile(githubAccessToken);
		User user = updateUserInfo(userProfile);

		String accessToken = tokenService.generateAccessToken(user.getUserId());
		String refreshToken = tokenService.generateRefreshToken();
		tokenService.saveRefreshToken(accessToken, refreshToken, user.getUserId());
		return LoginResponse.of(user, accessToken, refreshToken);
	}

	private String getGithubAccessTokenResponse(String code) {
		try {
			return webClient.post()
				.uri(GITHUB_TOKEN_URL)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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

	private String extractGithubAccessToken(String response) {
		String prefix = "access_token=";
		String suffix = "&";
		int startIndex = response.indexOf(prefix) + prefix.length();
        int endIndex = response.indexOf(suffix, startIndex);
        return response.substring(startIndex, endIndex);
	}

	private GithubUserProfile getUserProfile(String accessToken) {
		GithubUserProfile profile;

		try {
			profile = webClient.get()
				.uri(GITHUB_USER_PROFILE_URL)
				.header("Authorization", String.format("Bearer %s", accessToken))
				.retrieve()
				.toEntity(GithubUserProfile.class)
				.block()
				.getBody();
		} catch (WebClientResponseException | NullPointerException e) {
			log.error("Invalid Token {} : {}", accessToken, e.getMessage());
			throw new UnauthorizedApplicationException("유효하지 않은 토큰입니다.");
		}

		log.info("Github Profile: {}", profile.name());
		return profile;
	}

	@Transactional
	protected User updateUserInfo(GithubUserProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		User user = userRepository.findByUsername(userProfile.username())
			.orElseGet(
				() -> userRepository.save(userProfile.toUser(languageService.getLanguageByName("NOT_SELECTED"))));
		user.changeName(userProfile.name());
		user.changeProfileImg(userProfile.profileImg());
		return user;
	}

	@Transactional
	public AccessTokenResponse refresh(RefreshTokenRequest request) {
		String accessToken = request.accessToken();
		String refreshToken = request.refreshToken();
		String newAccessToken = tokenService.generateNewAccessToken(accessToken, refreshToken);
		return AccessTokenResponse.of(newAccessToken);
	}

	//로컬 테스트용
	public void addUser() {
		User user = User.builder()
			.name("name")
			.username("username")
			.nickname("닉네임")
			.profileImg("profileImg")
			.githubUrl(null)
			.language(languageService.getLanguageByName("JavaScript"))
			.goal(0)
			.successRate(0)
			.withdraw(false)
			.build();
		userRepository.save(user);
	}
}
