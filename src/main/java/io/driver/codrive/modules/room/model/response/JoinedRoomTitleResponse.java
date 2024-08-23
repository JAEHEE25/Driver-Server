package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record JoinedRoomTitleResponse(
	@Schema(description = "참여 중인 그룹 제목 목록")
	List<RoomTitleResponse> joinedRooms
) {
	public static JoinedRoomTitleResponse of(List<Room> joinedRooms) {
		return JoinedRoomTitleResponse.builder()
			.joinedRooms(RoomTitleResponse.of(joinedRooms))
			.build();
	}

	@Builder
	record RoomTitleResponse(
		@Schema(description = "그룹 ID", example = "1")
		Long roomId,

		@Schema(description = "그룹 제목", example = "그룹 제목")
		String title
	) {
		public static List<RoomTitleResponse> of(List<Room> rooms) {
			return rooms.stream()
				.map(RoomTitleResponse::of)
				.toList();
		}

		public static RoomTitleResponse of(Room room) {
			return RoomTitleResponse.builder()
				.roomId(room.getRoomId())
				.title(room.getTitle())
				.build();
		}
	}
}
