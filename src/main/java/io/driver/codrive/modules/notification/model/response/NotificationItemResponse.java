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

	@Schema(description = "알림 타입", allowableValues = {"CONNECT_START", "CREATED_PUBLIC_ROOM_REQUEST",
		"CREATED_PRIVATE_ROOM_JOIN", "PUBLIC_ROOM_REQUEST", "PUBLIC_ROOM_APPROVE", "ROOM_STATUS_INACTIVE", "FOLLOW"},
		example = "FOLLOW")
	String type,

	@Schema(description = "데이터 ID (그룹 관련 이벤트일 경우 roomId, 팔로우 관련 이벤트일 경우 followId)", example = "1")
	Long dataId,

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
			.dataId(notification.getDataId())
			.createdAt(DateUtils.formatCreatedAtByMdHm(notification.getCreatedAt()))
			.isRead(notification.getIsRead())
			.build();
	}
}
