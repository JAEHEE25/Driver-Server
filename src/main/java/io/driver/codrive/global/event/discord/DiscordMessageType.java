package io.driver.codrive.global.event.discord;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscordMessageType {
	JOIN("%s님이 회원가입하셨습니다! 환영합니다🥳"),
	LEAVE("%s님이 탈퇴하셨습니다...안녕히가세요😥"),
	GROUP_CREATE("%s님이 [%s] 그룹을 생성하셨습니다! 오늘도 파이팅😎");

    private final String message;

	public String formatMessage(String... args) {
		return String.format(message, args);
	}

}
