package io.driver.codrive.modules.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
	// 연결
	CONNECT_START("[%s] 알림 스트림이 연결되었습니다.", NotificationCategory.CONNECT, 50),

	// 그룹
	CREATED_PUBLIC_ROOM_REQUEST("[%s] 에 새로운 신청이 들어왔습니다", NotificationCategory.ROOM, 9),
	CREATED_PRIVATE_ROOM_JOIN("[%s] 에 [%s] 님이 참여하였습니다", NotificationCategory.ROOM, 5),
	PUBLIC_ROOM_REQUEST("[%s] 에 승인되면 즉시 알려드릴게요 :)", NotificationCategory.ROOM, 8),
	PUBLIC_ROOM_APPROVE("[%s] 그룹에 승인되었습니다", NotificationCategory.ROOM, 13),
	ROOM_STATUS_INACTIVE("[%s] 그룹 활동이 종료되었습니다", NotificationCategory.ROOM, 10),

	// 팔로우
	FOLLOW("[%s] 님이 회원님을 팔로우 했습니다", NotificationCategory.FOLLOW, 50);

	private final String message;
	private final NotificationCategory category;
	private final int length;

	public String formatMessage(String... args) {
		return String.format(message, args);
	}
}
