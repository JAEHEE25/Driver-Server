package io.driver.codrive.global.discord;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscordEventMessage {
	JOIN("회원가입하셨습니다! 환영합니다🥳"),
	LEAVE("탈퇴하셨습니다...안녕히가세요😥"),
	GROUP_CREATE("] 그룹을 생성하셨습니다! 오늘도 파이팅😎");

    private final String message;

	public String getUserMessage(String nickname) {
		return nickname + "님이 " + message;
	}

	public String getGroupMessage(String nickname, String groupTitle) {
		return nickname + "님이 [" + groupTitle + message;
	}
}
