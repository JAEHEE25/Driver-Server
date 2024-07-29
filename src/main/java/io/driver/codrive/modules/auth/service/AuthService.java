package io.driver.codrive.modules.auth.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.driver.codrive.modules.auth.model.GithubUserProfile;
import io.driver.codrive.modules.auth.model.LoginRequest;
import io.driver.codrive.modules.auth.model.LoginResponse;
import io.driver.codrive.modules.auth.model.SampleDto;
import io.driver.codrive.modules.global.jwt.JwtProvider;
import io.driver.codrive.modules.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final LanguageService languageService;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final WebClient webClient = WebClient.create();

	@Transactional
	public LoginResponse socialLogin(LoginRequest request) {
		GithubUserProfile userProfile = getUserProfile(request.accessToken());
		User user = updateUserInfo(userProfile);
		String accessToken = jwtProvider.generateAccessToken(user.getUserId());
		return LoginResponse.of(user, accessToken);
	}

	private GithubUserProfile getUserProfile(String accessToken) {
		GithubUserProfile profile;

		try {
			profile = webClient.get()
				.uri("https://api.github.com/user")
				.header("Authorization", String.format("Bearer %s", accessToken))
				.retrieve()
				.toEntity(GithubUserProfile.class)
				.block()
				.getBody();
		} catch (WebClientResponseException e) {
			throw new UnauthorizedApplicationException("유효하지 않은 토큰입니다.");
		}

		log.info("Github Profile: {}", profile.email());
		return profile;
	}

	@Transactional
	protected User updateUserInfo(GithubUserProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		User user = userRepository.findByEmail(userProfile.email())
			.orElseGet(() -> userRepository.save(userProfile.toUser(languageService.getLanguageByName("NOT_SELECTED"))));

		user.changeName(userProfile.name());
		user.changeProfileUrl(userProfile.profileUrl());

		return user;
	}

}
