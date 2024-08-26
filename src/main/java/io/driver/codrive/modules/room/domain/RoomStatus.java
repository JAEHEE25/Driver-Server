package io.driver.codrive.modules.room.domain;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoomStatus {
	CLOSED, ACTIVE, INACTIVE;

	public static RoomStatus getRoomStatusByName(String requestStatus) {
		return Arrays.stream(values()).filter(status -> status.name().equals(requestStatus))
			.findFirst().orElseThrow(() -> new IllegalArgumentException("지원하지 않는 상태 타입입니다."));
	}
}
