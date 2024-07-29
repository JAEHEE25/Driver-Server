package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.model.RoomDetailResponse;
import lombok.Builder;

@Builder
public record JoinedRoomListResponse(
	List<RoomDetailResponse> joinedRooms
) {
	public static JoinedRoomListResponse of(List<Room> joinedRooms) {
		return JoinedRoomListResponse.builder()
				.joinedRooms(RoomDetailResponse.of(joinedRooms))
				.build();
	}
}