package io.driver.codrive.global.discord;

import lombok.Builder;

@Builder
public record DiscordMessage(
	String content
) {
	public static DiscordMessage of(String message) {
		return DiscordMessage.builder()
				.content(message)
				.build();
	}
}