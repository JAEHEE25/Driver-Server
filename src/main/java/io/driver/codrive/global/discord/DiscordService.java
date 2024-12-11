package io.driver.codrive.global.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiscordService {
	private static final Logger log = LoggerFactory.getLogger(DiscordService.class);

	@Value("${discord.webhook-url}")
	private String webhookUrl;

	private final WebClient webClient = WebClient.create(webhookUrl);

    @Async
	public void sendMessage(DiscordEventMessage eventMessage, String nickname) {
        DiscordMessage discordMessage = DiscordMessage.of(eventMessage.getUserMessage(nickname));
        sendMessageToDiscord(discordMessage);
    }

	@Async
    public void sendMessage(DiscordEventMessage eventMessage, String nickname, String groupTitle) {
        DiscordMessage discordMessage = DiscordMessage.of(eventMessage.getGroupMessage(nickname, groupTitle));
        sendMessageToDiscord(discordMessage);
    }

    private void sendMessageToDiscord(DiscordMessage discordMessage) {
        try {
            webClient.post()
					.uri(webhookUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(discordMessage)
					.retrieve()
					.bodyToMono(Void.class)
					.block();
        } catch (Exception e) {
			log.info(e.getMessage());
            throw new InternalServerErrorApplicationException("Discord message 전송에 실패하였습니다.");
        }
    }
}
