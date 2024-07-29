package io.driver.codrive.modules.room.model;

import java.util.List;

import lombok.Builder;

@Builder
public record RoomRecommendResponse(
	List<RoomDetailResponse> recommendRooms
) {

	public static RoomRecommendResponse of(List<RoomDetailResponse> recommendRooms) {
		return RoomRecommendResponse.builder()
				.recommendRooms(recommendRooms)
				.build();
	}
}
