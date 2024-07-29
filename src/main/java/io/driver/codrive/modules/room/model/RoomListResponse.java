package io.driver.codrive.modules.room.model;

import java.util.List;

import lombok.Builder;

@Builder
public record RoomListResponse(
	List<RoomDetailResponse> rooms

) {
	public static RoomListResponse of(List<RoomDetailResponse> rooms) {
		return RoomListResponse.builder()
				.rooms(rooms)
				.build();
	}

}
