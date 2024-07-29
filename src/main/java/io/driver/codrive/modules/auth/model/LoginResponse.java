package io.driver.codrive.modules.auth.model;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "소셜 로그인 코드", example = "닉네임")
	String nickname,

	@Schema(description = "발급한 Access Token", example = "ACCESS_TOKEN")
	String accessToken
) {
	public static LoginResponse of(User user, String accessToken) {
		return LoginResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.accessToken(accessToken)
			.build();
	}
}
