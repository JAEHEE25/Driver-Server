package io.driver.codrive.modules.user.model;

import io.driver.codrive.modules.user.domain.Language;
import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record UserInfoResponse (
	String name,
	String nickname,
	String profileUrl,
	String githubUrl,
	Language language,
	Integer level
) {
	public static UserInfoResponse of(User user) {
		return UserInfoResponse.builder()
			.name(user.getName())
			.nickname(user.getNickname())
			.profileUrl(user.getProfileUrl())
			.githubUrl(user.getGithubUrl())
			.language(user.getLanguage())
			.level(user.getLevel())
			.build();
	}
}
