package io.driver.codrive.modules.room.domain;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoomStatus {
	CLOSED("모집 마감"), ACTIVE("활동 중"), INACTIVE("활동 종료");

	private final String name;

	public static RoomStatus getRoomStatusByName(String requestStatus) {
		return Arrays.stream(values()).filter(status -> status.name.equals(requestStatus))
			.findFirst().orElseThrow(() -> new IllegalArgumentException("지원하지 않는 상태 타입입니다."));
	}
}
