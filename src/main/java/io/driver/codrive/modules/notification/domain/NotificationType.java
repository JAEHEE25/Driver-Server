package io.driver.codrive.modules.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
	CONNECT_START("[%s] 알림 스트림이 연결되었습니다."),
	CREATED_ROOM_REQUEST("%s에 새로운 신청이 들어왔습니다."),
	PUBLIC_ROOM_REQUEST("%s 그룹에 신청하였습니다. 승인되면 즉시 알려드릴게요 :)"),
	PUBLIC_ROOM_APPROVE("%s 그룹에 승인되었습니다."),
	PRIVATE_ROOM_JOIN("%s 비밀그룹에 %s 님이 참여하였습니다."),
	ROOM_STATUS_INACTIVE("%s 그룹의 활동이 종료되었습니다."),
	FOLLOW("%s님이 회원님을 팔로우 했습니다.");

	private final String message;

	public String formatMessage(String... args) {
		return String.format(message, (Object)args);
	}
}
