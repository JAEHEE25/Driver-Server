package io.driver.codrive.modules.room.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.model.OwnerDetailResponse;
import lombok.Builder;

@Builder
public record RoomDetailResponse(
		Long roomId,
		OwnerDetailResponse owner,
		String title,
		String imageUrl,
		Integer capacity,
		List<String> languages,
		String introduction,
		String information
) {
	public static List<RoomDetailResponse> of(List<Room> rooms) {
		return rooms.stream()
				.map(RoomDetailResponse::of)
				.toList();
	}

	public static RoomDetailResponse of(Room room) {
		return RoomDetailResponse.builder()
				.roomId(room.getRoomId())
				.title(room.getTitle())
				.imageUrl(room.getImageUrl())
				.capacity(room.getCapacity())
				.languages(room.getLanguages())
				.introduction(room.getIntroduction())
				.information(room.getInformation())
				.build();
	}
}