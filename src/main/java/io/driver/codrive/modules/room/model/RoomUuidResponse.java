package io.driver.codrive.modules.room.model;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomUuidResponse(
	@Schema(description = "그룹 ID", example = "1")
	Long roomId,

	@Schema(description = "비밀번호", example = "비밀번호")
	String password,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc
) {
	public static RoomUuidResponse of(Room room) {
		return RoomUuidResponse.builder()
			.roomId(room.getRoomId())
			.password(room.getPassword())
			.imageSrc(room.getImageSrc())
			.build();
	}
}
