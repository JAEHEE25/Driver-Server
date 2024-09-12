package io.driver.codrive.modules.auth.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "소셜 로그인 코드", example = "닉네임")
	String nickname,

	@Schema(description = "프로필 이미지", example = "PROFILE_IMG")
	String profileImg,

	@Schema(description = "주 언어", example = "Java")
	String langauge,

	@Schema(description = "사용자 존재 여부", example = "true")
	boolean isExistUser,

	@Schema(description = "발급한 Access Token", example = "ACCESS_TOKEN")
	String accessToken,

	@Schema(description = "발급한 Refresh Token", example = "REFRESH_TOKEN")
	String refreshToken
) {
	public static LoginResponse of(User user, boolean isExistUser, String accessToken, String refreshToken) {
		return LoginResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImg(user.getProfileImg())
			.langauge(user.getLanguage().getName())
			.isExistUser(isExistUser)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
