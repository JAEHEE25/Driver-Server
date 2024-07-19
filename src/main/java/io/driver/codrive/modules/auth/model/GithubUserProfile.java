package io.driver.codrive.modules.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.driver.codrive.modules.user.domain.Language;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;

public record GithubUserProfile(
	@JsonProperty("email")
	String email,

	@JsonProperty("login")
	String userName,

	@JsonProperty("avatar_url")
	String profileUrl
) {
	public User toUser() { //todo 레벨 정보 추가
		return User.builder()
			.email(email)
			.userName(userName)
			.nickname(userName)
			.profileUrl(profileUrl)
			.comment(null)
			.language(Language.NOT_SELECETED)
			.role(Role.MEMBER)
			.withdraw(false)
			.build();
	}
}
