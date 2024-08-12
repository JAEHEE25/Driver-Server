package io.driver.codrive.modules.room.model.response;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomCreateResponse(
	@Schema(description = "그룹 ID", example = "1")
	Long roomId,

	@Schema(description = "비밀번호", example = "비밀번호")
	String password,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "그룹 UUID", example = "12345678")
	String uuid
) {
	public static RoomCreateResponse of(Room room) {
		return RoomCreateResponse.builder()
			.roomId(room.getRoomId())
			.password(room.getPassword())
			.imageSrc(room.getImageSrc())
			.uuid(room.getUuid())
			.build();
	}
}
