package io.driver.codrive.modules.notification.model.response;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NotificationItemResponse(
	@Schema(description = "알림 ID", example = "1")
	Long notificationId,

	@Schema(description = "알림 내용", example = "알림 내용")
	String content,

	@Schema(description = "알림 타입",
		allowableValues = {"CONNECT_START", "GROUP_REQUEST", "GROUP_APPROVE", "FOLLOW"},example = "FOLLOW")
	String type,

	@Schema(description = "알림 생성 일시", example = "2/5 12:00")
	String createdAt,

	@Schema(description = "알림 읽음 여부", example = "false")
	Boolean isRead
) {
	public static NotificationItemResponse of(Notification notification) {
		return NotificationItemResponse.builder()
			.notificationId(notification.getNotificationId())
			.content(notification.getContent())
			.type(notification.getNotificationType().name())
			.createdAt(DateUtils.formatCreatedAtByMdHm(notification.getCreatedAt()))
			.isRead(notification.getIsRead())
			.build();
	}
}
