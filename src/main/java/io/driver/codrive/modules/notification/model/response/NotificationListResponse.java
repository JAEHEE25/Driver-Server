package io.driver.codrive.modules.notification.model.response;

import java.util.List;

import io.driver.codrive.modules.notification.domain.Notification;
import lombok.Builder;

@Builder
public record NotificationListResponse(
	List<NotificationItemResponse> notifications
) {
	public static NotificationListResponse of(List<Notification> notifications) {
		return NotificationListResponse.builder()
			.notifications(notifications.stream().map(NotificationItemResponse::of).toList())
			.build();
	}
}
