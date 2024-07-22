package io.driver.codrive.modules.room.model;

import lombok.Builder;

@Builder
public record RoomCreateResponse(
	Long roomId
) {
	public static RoomCreateResponse of(Long roomId) {
		return RoomCreateResponse.builder().roomId(roomId).build();
	}
}
