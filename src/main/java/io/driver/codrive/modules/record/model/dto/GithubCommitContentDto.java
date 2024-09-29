package io.driver.codrive.modules.record.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubCommitContentDto {
	private String message;
	private String content;

	public static GithubCommitContentDto of(String message, String content) {
		return GithubCommitContentDto.builder()
			.message(message)
			.content(content)
			.build();
	}
}
