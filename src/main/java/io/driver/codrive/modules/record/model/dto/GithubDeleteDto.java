package io.driver.codrive.modules.record.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GithubDeleteDto {
	private String message;
	private String sha;

	public static GithubDeleteDto of(String message, String sha) {
		return GithubDeleteDto.builder()
			.message(message)
			.sha(sha)
			.build();
	}
}
