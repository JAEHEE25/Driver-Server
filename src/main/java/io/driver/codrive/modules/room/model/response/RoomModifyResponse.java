package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomModifyResponse(
	@Schema(description = "그룹 제목", example = "그룹 제목")
	String title,

	@Schema(description = "비밀번호", example = "비밀번호")
	String password,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "모집 인원", example = "20")
	int capacity,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "그룹 한 줄 소개", example = "그룹 한 줄 소개")
	String introduce,

	@Schema(description = "진행 방식", example = "진행 방식")
	String information
) {
	public static RoomModifyResponse of(Room room) {
		return RoomModifyResponse.builder()
			.title(room.getTitle())
			.password(room.getPassword())
			.imageSrc(room.getImageSrc())
			.capacity(room.getCapacity())
			.tags(room.getLanguages())
			.introduce(room.getIntroduce())
			.information(room.getInformation())
			.build();
	}
}