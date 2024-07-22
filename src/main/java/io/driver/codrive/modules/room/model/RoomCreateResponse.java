package io.driver.codrive.modules.room.model;

import io.driver.codrive.modules.room.domain.Room;
import lombok.Builder;

@Builder
public record RoomCreateResponse(
	Long roomId
) {
	public static RoomCreateResponse of(Room room) {
		return RoomCreateResponse.builder().roomId(room.getRoomId()).build();
	}
}
