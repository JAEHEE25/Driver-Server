package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record JoinedRoomListResponse(
	@Schema(description = "참여 중인 그룹 목록 총 페이지 개수", examples = "2")
	int totalPage,

	@Schema(description = "참여 중인 그룹 목록")
	List<RoomItemResponse> joinedRooms
) {
	public static JoinedRoomListResponse of(int totalPage, List<Room> joinedRooms) {
		return JoinedRoomListResponse.builder()
			.totalPage(totalPage)
			.joinedRooms(RoomItemResponse.of(joinedRooms))
			.build();
	}
}