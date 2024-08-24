package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecentRoomResponse(
	@Schema(description = "최근 활동 중인 그룹 목록")
	List<RecentRoomItemResponse> rooms
) {
	public static RecentRoomResponse of(List<Room> rooms) {
		return RecentRoomResponse.builder()
			.rooms(RecentRoomItemResponse.of(rooms))
			.build();
	}

	@Builder
	record RecentRoomItemResponse(
		@Schema(description = "그룹 ID", example = "1")
		Long roomId,

		@Schema(description = "그룹 언어 태그 목록", example = "[\"Java\", \"Python\"]")
		List<String> tags,

		@Schema(description = "그룹 제목", example = "그룹 제목")
		String title,

		@Schema(description = "그룹 소개", example = "그룹 소개")
		String introduce
	) {
		public static List<RecentRoomItemResponse> of(List<Room> rooms) {
			return rooms.stream()
				.map(RecentRoomItemResponse::of)
				.toList();
		}

		public static RecentRoomItemResponse of(Room room) {
			return RecentRoomItemResponse.builder()
				.roomId(room.getRoomId())
				.tags(room.getLanguages())
				.title(room.getTitle())
				.introduce(room.getIntroduce())
				.build();
		}
	}
}
