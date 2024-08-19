package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomListResponse(
	@Schema(description = "그룹 목록 총 페이지 개수", examples = "5")
	int totalPage,

	@Schema(description = "그룹 목록")
	List<RoomItemResponse> rooms

) {
	public static RoomListResponse of(int totalPage, List<Room> rooms) {
		return RoomListResponse.builder()
			.totalPage(totalPage)
			.rooms(RoomItemResponse.of(rooms))
			.build();
	}
}
