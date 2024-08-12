package io.driver.codrive.modules.user.model.response;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProfileChangeResponse(
	@Schema(description = "닉네임", example = "닉네임", minLength = 1, maxLength = 10)
	String nickname,

	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "한 줄 소개", example = "한 줄 소개")
	String comment,

	@Schema(description = "GitHub URL", example = "GITHUB_URL")
	String githubUrl
) {
	public static ProfileChangeResponse of(User user) {
		return ProfileChangeResponse.builder()
			.nickname(user.getNickname())
			.language(user.getLanguage().getName())
			.comment(user.getComment())
			.githubUrl(user.getGithubUrl())
			.build();
	}
}
