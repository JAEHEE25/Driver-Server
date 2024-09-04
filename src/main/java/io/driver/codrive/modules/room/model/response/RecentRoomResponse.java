package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecentRoomResponse(
	@Schema(description = "최근 활동 중인 그룹 목록")
	List<RecentRoomItemResponse> rooms
) {
	public static RecentRoomResponse of(List<Room> rooms, User user) {
		return RecentRoomResponse.builder()
			.rooms(RecentRoomItemResponse.of(rooms, user))
			.build();
	}

	@Builder
	record RecentRoomItemResponse(
		@Schema(description = "그룹 ID", example = "1")
		Long roomId,

		@Schema(description = "그룹장 사용자 ID", example = "1")
		Long ownerId,

		@Schema(description = "그룹 언어 태그 목록", example = "[\"Java\", \"Python\"]")
		List<String> tags,

		@Schema(description = "그룹 제목", example = "그룹 제목")
		String title,

		@Schema(description = "그룹 소개", example = "그룹 소개")
		String introduce,

		@Schema(description = "해당 그룹의 멤버인지 여부", example = "true")
		boolean isMember,

		@Schema(description = "공개 그룹인지 여부", example = "true")
		boolean isPublicRoom

	) {
		public static List<RecentRoomItemResponse> of(List<Room> rooms, User user) {
			return rooms.stream()
				.map(room -> RecentRoomItemResponse.of(room, room.hasMember(user)))
				.toList();
		}

		public static RecentRoomItemResponse of(Room room, boolean isMember) {
			return RecentRoomItemResponse.builder()
				.roomId(room.getRoomId())
				.ownerId(room.getOwner().getUserId())
				.tags(room.getLanguages())
				.title(room.getTitle())
				.introduce(room.getIntroduce())
				.isMember(isMember)
				.isPublicRoom(room.isPublicRoom())
				.build();
		}
	}
}
