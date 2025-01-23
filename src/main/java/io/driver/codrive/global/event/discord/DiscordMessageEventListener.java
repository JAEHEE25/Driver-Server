package io.driver.codrive.global.event.discord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.modules.room.event.RoomCreatedEvent;
import io.driver.codrive.modules.user.event.UserJoinedEvent;
import io.driver.codrive.modules.user.event.UserWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordMessageEventListener {
	@Value("${discord.webhook-url}")
	private String webhookUrl;

	private final WebClient webClient = WebClient.create(webhookUrl);

	@EventListener
	@Async
	public void sendUserWithdrawnMessage(UserWithdrawnEvent event) {
		DiscordMessage message = DiscordMessage.of(DiscordMessageType.LEAVE.formatMessage(event.nickname()));
		sendMessageToDiscord(message);
	}

	@EventListener
	@Async
	public void sendUserJoinedMessage(UserJoinedEvent event) {
		DiscordMessage message = DiscordMessage.of(DiscordMessageType.JOIN.formatMessage(event.nickname()));
		sendMessageToDiscord(message);
	}

	@EventListener
	@Async
	public void sendRoomCreatedMessage(RoomCreatedEvent event) {
		DiscordMessage message = DiscordMessage.of(DiscordMessageType.GROUP_CREATE.formatMessage(event.nickname(),
			event.roomTitle()));
		sendMessageToDiscord(message);
	}

	private void sendMessageToDiscord(DiscordMessage message) {
		try {
			webClient.post()
				.uri(webhookUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(message)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		} catch (Exception e) {
			log.error("Discord message 전송 실패 : {}", e.getMessage());
			throw new InternalServerErrorApplicationException("Discord message 전송에 실패하였습니다.");
		}
	}
}
