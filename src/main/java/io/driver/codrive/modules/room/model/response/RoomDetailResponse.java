package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomDetailResponse(
	@Schema(description = "그룹 UUID", example = "12345678")
	String uuid,

	@Schema(description = "그룹 제목", example = "그룹 제목")
	String title,

	@Schema(description = "비밀번호", example = "비밀번호")
	String password,

	@Schema(description = "그룹장", implementation = OwnerDetailResponse.class)
	OwnerDetailResponse owner,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "신청 인원", example = "10")
	int requestedCount,

	@Schema(description = "모집 인원", example = "20")
	int capacity,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "그룹 한 줄 소개", example = "그룹 한 줄 소개")
	String introduce,

	@Schema(description = "진행 방식", example = "진행 방식")
	String information,

	@Schema(description = "해당 그룹의 멤버인지 여부", example = "true")
	boolean isMember,

	@Schema(description = "공개 그룹인지 여부", example = "true")
	boolean isPublicRoom
) {
	public static RoomDetailResponse of(Room room, int requestedCount, boolean isMember) {
		return RoomDetailResponse.builder()
				.uuid(room.getUuid())
				.title(room.getTitle())
				.password(room.getPassword())
				.owner(io.driver.codrive.modules.room.model.response.OwnerDetailResponse.of(room.getOwner()))
				.imageSrc(room.getImageSrc())
				.requestedCount(requestedCount)
				.capacity(room.getCapacity())
				.tags(room.getLanguages())
				.introduce(room.getIntroduce())
				.information(room.getInformation())
				.isMember(isMember)
				.isPublicRoom(room.isPublicRoom())
				.build();
	}
}