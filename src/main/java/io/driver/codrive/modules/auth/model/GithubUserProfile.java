package io.driver.codrive.modules.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;

public record GithubUserProfile(
	@JsonProperty("email")
	String email,

	@JsonProperty("name")
	String name,

	@JsonProperty("avatar_url")
	String profileImg
) {
	public User toUser(Language language) {
		return User.builder()
			.email(email)
			.name(name)
			.nickname(name)
			.profileImg(profileImg)
			.githubUrl(null)
			.language(language)
			.level(1)
			.role(Role.MEMBER)
			.withdraw(false)
			.build();
	}
}
