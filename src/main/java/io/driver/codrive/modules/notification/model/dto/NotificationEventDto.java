package io.driver.codrive.modules.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.driver.codrive.modules.notification.domain.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEventDto {
	private Long notificationId;

	private Long userId;

	private String content;

	private String notificationType;

	private Long dataId;

	@JsonProperty("isRead")
	private boolean isRead;

	public NotificationEventDto(Notification notification) {
		this.notificationId = notification.getNotificationId();
		this.userId = notification.getUser().getUserId();
		this.content = notification.getContent();
		this.notificationType = notification.getNotificationType().name();
		this.dataId = notification.getDataId();
		this.isRead = false;
	}
}
