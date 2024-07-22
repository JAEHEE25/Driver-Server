package io.driver.codrive.modules.room.model;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import lombok.Builder;

@Builder
public record RoomDetailResponse(
		Long roomId,
		String title,
		String password,
		String imageUrl,
		Integer capacity,
		List<String> languages,
		String introduction,
		String information
) {
	public static RoomDetailResponse of(Room room) {
		return RoomDetailResponse.builder()
				.roomId(room.getRoomId())
				.title(room.getTitle())
				.password(room.getPassword())
				.imageUrl(room.getImageUrl())
				.capacity(room.getCapacity())
				.languages(room.getLanguages())
				.introduction(room.getIntroduction())
				.information(room.getInformation())
				.build();
	}
}