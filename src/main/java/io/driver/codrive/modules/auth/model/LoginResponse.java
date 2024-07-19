package io.driver.codrive.modules.auth.model;

import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record LoginResponse(
	Long userId,
	String nickname,
	String profileUrl,
	String accessToken,
	String refreshToken
) {
	public static LoginResponse of(User user, String accessToken, String refreshToken) {
		return LoginResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileUrl(user.getProfileUrl())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
