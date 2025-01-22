package io.driver.codrive.global.event.discord;

import lombok.Builder;

@Builder
public record DiscordMessage (
	String content
){
	public static DiscordMessage of(String content) {
		return DiscordMessage.builder().content(content).build();
	}
}
