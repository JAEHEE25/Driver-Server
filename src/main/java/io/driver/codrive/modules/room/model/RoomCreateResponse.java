package io.driver.codrive.modules.room.model;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomCreateResponse(
	@Schema(description = "그룹 ID", example = "1")
	Long roomId
) {
	public static RoomCreateResponse of(Room room) {
		return RoomCreateResponse.builder().roomId(room.getRoomId()).build();
	}
}
