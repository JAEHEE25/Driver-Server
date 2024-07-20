package io.driver.codrive.modules.user.model;

import io.driver.codrive.modules.user.domain.Language;
import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record ProfileChangeResponse(
	String nickname,
	Language language,
	String githubUrl
) {
	public static ProfileChangeResponse of(User user) {
		return ProfileChangeResponse.builder()
			.nickname(user.getNickname())
			.language(user.getLanguage())
			.githubUrl(user.getGithubUrl())
			.build();
	}
}
