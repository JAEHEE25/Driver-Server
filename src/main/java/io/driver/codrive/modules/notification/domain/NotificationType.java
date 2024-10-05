package io.driver.codrive.modules.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
	CONNECT_START("[%s] 알림 스트림이 연결되었습니다."),
	GROUP_REQUEST("[%s] 그룹에 새로운 신청자가 있어요!"),
	GROUP_APPROVE("[%s] 그룹에 승인되었어요!"),
	FOLLOW("%s님이 회원님을 팔로우하기 시작했어요!"),;

	private final String message;

	public String formatMessage(String arg) {
		return String.format(message, arg);
	}
}
