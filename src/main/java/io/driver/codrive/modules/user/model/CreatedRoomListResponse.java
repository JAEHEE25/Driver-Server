package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.model.RoomDetailResponse;
import lombok.Builder;

@Builder
public record CreatedRoomListResponse(
	List<RoomDetailResponse> createdRooms
) {
	public static CreatedRoomListResponse of(List<Room> createdRooms) {
		return CreatedRoomListResponse.builder()
				.createdRooms(RoomDetailResponse.of(createdRooms))
				.build();
	}
}