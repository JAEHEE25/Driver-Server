package io.driver.codrive.modules.auth.model;

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
		return User.builder()
			.name(name)
			.username(username)
			.nickname(name)
			.profileImg(profileImg)
			.githubUrl(null)
			.language(language)
			.goal(0)
			.successRate(0)
			.withdraw(false)
			.build();
	}
}
