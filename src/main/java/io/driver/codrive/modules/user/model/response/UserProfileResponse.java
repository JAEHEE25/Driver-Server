package io.driver.codrive.modules.user.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserProfileResponse(
	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "성과율", example = "30")
	int successRate,

	@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
	String profileImg,

	@Schema(description = "닉네임", example = "닉네임")
	String nickname,

	@Schema(description = "GitHub URL", example = "GITHUB_URL")
	String githubUrl,

	@Schema(description = "한 줄 소개", example = "한 줄 소개")
	String comment,

	@Schema(description = "팔로우 여부 (본인일 경우 null)", example = "true")
	Boolean isFollowing
) {
	public static UserProfileResponse of(User user, Boolean isFollowing) {
		return UserProfileResponse.builder()
			.language(user.getLanguage().getName())
			.successRate(user.getSuccessRate())
			.profileImg(user.getProfileImg())
			.nickname(user.getNickname())
			.githubUrl(user.getGithubUrl())
			.comment(user.getComment())
			.isFollowing(isFollowing)
			.build();
	}
}
