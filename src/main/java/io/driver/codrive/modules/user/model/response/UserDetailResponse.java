package io.driver.codrive.modules.user.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserDetailResponse(
	@Schema(description = "이름", example = "이름")
	String name,

	@Schema(description = "닉네임", example = "닉네임")
	String nickname,

	@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
	String profileImg,

	@Schema(description = "한 줄 소개", example = "한 줄 소개")
	String comment,

	@Schema(description = "GitHub URL", example = "GITHUB_URL")
	String githubUrl,

	@Schema(description = "GitHub Repository 이름", example = "Algorithm")
	String githubRepositoryName,

	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "목표 개수", example = "1")
	int goal

) {
	public static UserDetailResponse of(User user) {
		return UserDetailResponse.builder()
			.name(user.getName())
			.nickname(user.getNickname())
			.profileImg(user.getProfileImg())
			.comment(user.getComment())
			.githubUrl(user.getGithubUrl())
			.githubRepositoryName(user.getGithubRepositoryName())
			.language(user.getLanguage().getName())
			.goal(user.getGoal())
			.build();
	}
}
