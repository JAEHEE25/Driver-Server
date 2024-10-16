package io.driver.codrive.modules.notification.model.dto;

import io.driver.codrive.modules.notification.domain.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEventDto {
	private Long notificationId;

	private String content;

	private String notificationType;

	private Long userId;

	public NotificationEventDto(Notification notification) {
		this.notificationId = notification.getNotificationId();
		this.content = notification.getContent();
		this.notificationType = notification.getNotificationType().name();
		this.userId = notification.getUserId();
	}

}
