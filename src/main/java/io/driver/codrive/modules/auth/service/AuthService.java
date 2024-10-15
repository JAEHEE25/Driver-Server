package io.driver.codrive.modules.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.driver.codrive.global.discord.DiscordEventMessage;
import io.driver.codrive.global.discord.DiscordService;
import io.driver.codrive.modules.auth.model.request.GithubLoginRequest;
import io.driver.codrive.modules.auth.model.dto.GithubUserProfile;
import io.driver.codrive.modules.auth.model.request.RefreshTokenRequest;
import io.driver.codrive.modules.auth.model.response.AppAccessTokenResponse;
import io.driver.codrive.modules.auth.model.response.LoginResponse;
import io.driver.codrive.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.record.service.github.GithubTokenService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String GITHUB_USER_PROFILE_URL = "https://api.github.com/user";
	private static final String GITHUB_HOME_URL = "https://github.com/";
	private final LanguageService languageService;
	private final UserRepository userRepository;
	private final AppTokenService appTokenService;
	private final DiscordService discordService;
	private final GithubTokenService githubTokenService;
	private final WebClient webClient;

	@Transactional
	public LoginResponse socialLogin(GithubLoginRequest request) {
		String githubTokenResponse = githubTokenService.getGithubTokenResponse(request.code());
		String githubAccessToken = githubTokenService.extractGithubAccessToken(githubTokenResponse);
		GithubUserProfile userProfile = getUserProfile(githubAccessToken);
		boolean isExistUser = userRepository.existsByUsername(userProfile.username());
		User user = updateUserInfo(userProfile);

		if (!isExistUser) {
			discordService.sendMessage(DiscordEventMessage.JOIN, userProfile.username());
		}

		String appAccessToken = appTokenService.generateAccessToken(user.getUserId());
		String appRefreshToken = appTokenService.generateRefreshToken();
		Long userId = user.getUserId();
		appTokenService.saveAppToken(appAccessToken, appRefreshToken, userId);
		githubTokenService.saveGithubToken(userId, githubAccessToken, githubTokenResponse);
		return LoginResponse.of(user, isExistUser, appAccessToken, appRefreshToken);
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
		log.info("Github Profile: {}", profile.username());
		return profile;
	}

	@Transactional
	protected User updateUserInfo(GithubUserProfile userProfile) {
		String githubUrl = GITHUB_HOME_URL + userProfile.username();
		User user = userRepository.findByUsername(userProfile.username())
			.orElseGet(
				() -> userRepository.save(userProfile.toUser(languageService.getLanguageByName("사용언어"), githubUrl)));
		user.changeUserName(userProfile.username());
		user.changeProfileImg(userProfile.profileImg());
		user.changeGithubUrl(githubUrl);
		return user;
	}

	@Transactional
	public AppAccessTokenResponse refresh(RefreshTokenRequest request) {
		String accessToken = request.accessToken();
		String refreshToken = request.refreshToken();
		String newAccessToken = appTokenService.generateNewAccessToken(accessToken, refreshToken);
		return AppAccessTokenResponse.of(newAccessToken);
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
