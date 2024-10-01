package io.driver.codrive.modules.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.user.domain.User;

public record GithubUserProfile(
	@JsonProperty("login")
	String username,

	@JsonProperty("name")
	String name,

	@JsonProperty("avatar_url")
	String profileImg
) {
	public User toUser(Language language) {
		String githubName = name;
		if (githubName == null) githubName = username;

		return User.builder()
			.name(githubName)
			.username(username)
			.nickname(username)
			.profileImg(profileImg)
			.comment(null)
			.githubUrl(null)
			.githubRepositoryName(null)
			.language(language)
			.goal(0)
			.successRate(0)
			.solvedCount(0L)
			.withdraw(false)
			.build();
	}
}
