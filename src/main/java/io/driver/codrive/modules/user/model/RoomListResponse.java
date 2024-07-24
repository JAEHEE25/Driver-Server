package io.driver.codrive.modules.user.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record RoomListResponse(
	Long roomId,
	OwnerDetailResponse owner,
	String title,
	String imageUrl,
	Integer capacity,
	List<String> languages,
	String introduction
) {
	public static List<RoomListResponse> of(List<Room> rooms) {
		return rooms.stream()
				.map(RoomListResponse::of)
				.toList();
	}

	public static RoomListResponse of(Room room) {
		return RoomListResponse.builder()
				.roomId(room.getRoomId())
				.owner(OwnerDetailResponse.of(room.getOwner()))
				.title(room.getTitle())
				.imageUrl(room.getImageUrl())
				.capacity(room.getCapacity())
				.languages(room.getLanguages())
				.introduction(room.getIntroduction())
				.build();
	}

	@Builder
	record OwnerDetailResponse(
		Long userId,
		String nickname,
		String profileUrl
	) {
		public static OwnerDetailResponse of(User owner) {
			return OwnerDetailResponse.builder()
					.userId(owner.getUserId())
					.nickname(owner.getNickname())
					.profileUrl(owner.getProfileUrl())
					.build();
		}
	}
}
