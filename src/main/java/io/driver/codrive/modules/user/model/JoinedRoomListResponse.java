package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import lombok.Builder;

@Builder
public record JoinedRoomListResponse(
	List<RoomListResponse> joinedRooms
) {
	public static JoinedRoomListResponse of(List<Room> joinedRooms) {
		return JoinedRoomListResponse.builder()
				.joinedRooms(RoomListResponse.of(joinedRooms))
				.build();
	}
}