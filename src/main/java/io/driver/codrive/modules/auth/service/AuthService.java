package io.driver.codrive.modules.auth.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import io.driver.codrive.modules.auth.model.GithubUserProfile;
import io.driver.codrive.modules.auth.model.LoginRequest;
import io.driver.codrive.modules.auth.model.LoginResponse;
import io.driver.codrive.modules.auth.model.SampleDto;
import io.driver.codrive.modules.auth.service.jwt.JwtProvider;
import io.driver.codrive.modules.global.exception.UnauthorizedApplicationException;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final WebClient webClient = WebClient.create();

	@Transactional
	public LoginResponse socialLogin(LoginRequest request) {
		GithubUserProfile userProfile = getUserProfile(request.accessToken());
		User user = updateUserInfo(userProfile);

		String accessToken = jwtProvider.generateAccessToken(user.getUserId());
		String refreshToken = "REFRESH_TOKEN";

		return LoginResponse.of(user, accessToken, refreshToken);
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
		} catch (NullPointerException e) {
			throw new UnauthorizedApplicationException("유효하지 않은 토큰입니다.");
		}

		log.info("Github Profile Username: {}", profile.userName());
		return profile;
	}

	@Transactional
	protected User updateUserInfo(GithubUserProfile userProfile) {
		User user = userRepository.findByEmail(userProfile.userName())
			.orElseGet(() -> userRepository.save(userProfile.toUser()));

		user.changeUserName(userProfile.userName());
		user.changeProfileUrl(userProfile.profileUrl());

		return user;
	}


	//로컬 테스트용
	public String getAccessToken(String clientId, String clientSecret, String code) {
		String response = webClient.post()
			.uri("https://github.com/login/oauth/access_token")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.bodyValue(SampleDto.createRequest(clientId, clientSecret, code))
			.retrieve()
			.toEntity(String.class)
			.block()
			.getBody();
		log.info("access Token: " + response);
		return response;
	}
}
