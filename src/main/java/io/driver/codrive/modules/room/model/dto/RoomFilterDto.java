package io.driver.codrive.modules.room.model.dto;

import java.util.List;

import io.driver.codrive.modules.room.model.request.RoomFilterRequest;
import lombok.Builder;

@Builder
public record RoomFilterDto(
	List<Long> tagIds,
	Integer min,
	Integer max
) {
	public static RoomFilterDto toRoomFilterDto(RoomFilterRequest request, List<Long> tagIds) {
		return RoomFilterDto.builder()
			.tagIds(tagIds)
			.min(request.min())
			.max(request.max())
			.build();
	}
}
