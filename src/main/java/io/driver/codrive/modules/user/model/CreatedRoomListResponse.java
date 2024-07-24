package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import lombok.Builder;

@Builder
public record CreatedRoomListResponse(
	List<RoomListResponse> createdRooms
) {
	public static CreatedRoomListResponse of(List<Room> createdRooms) {
		return CreatedRoomListResponse.builder()
				.createdRooms(RoomListResponse.of(createdRooms))
				.build();
	}
}