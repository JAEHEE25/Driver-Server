package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomRecommendResponse(
	@Schema(description = "오늘의 추천 그룹 목록")
	List<RoomItemResponse> rooms
) {
	public static RoomRecommendResponse of(List<Room> rooms) {
		return RoomRecommendResponse.builder()
			.rooms(RoomItemResponse.of(rooms, false))
			.build();
	}
}
