package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomItemResponse(
	@Schema(description = "그룹 ID", example = "1")
	Long roomId,

	@Schema(description = "그룹 UUID", example = "12345678")
	String uuid,

	@Schema(description = "그룹 제목", example = "그룹 제목")
	String title,

	@Schema(description = "그룹장 프로필", implementation = OwnerDetailResponse.class)
	OwnerDetailResponse owner,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "현재 멤버 수", example = "15")
	int memberCount,

	@Schema(description = "모집 인원", example = "20")
	int capacity,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "그룹 한 줄 소개", example = "그룹 한 줄 소개")
	String introduce,

	@Schema(description = "해당 그룹의 멤버인지 여부", example = "true")
	boolean isMember,

	@Schema(description = "공개 그룹인지 여부", example = "true")
	boolean isPublicRoom
) {
	public static RoomItemResponse of(Room room, boolean isMember) {
		return RoomItemResponse.builder()
			.roomId(room.getRoomId())
			.uuid(room.getUuid())
			.title(room.getTitle())
			.owner(OwnerDetailResponse.of(room.getOwner()))
			.imageSrc(room.getImageSrc())
			.memberCount(room.getMemberCount())
			.capacity(room.getCapacity())
			.tags(room.getLanguages())
			.introduce(room.getIntroduce())
			.isMember(isMember)
			.isPublicRoom(room.isPublicRoom())
			.build();
	}

	public static List<RoomItemResponse> of(List<Room> rooms, User user) {
		return rooms.stream()
			.map(room -> RoomItemResponse.of(room, room.hasMember(user)))
			.toList();
	}

	public static List<RoomItemResponse> of(List<Room> rooms, boolean isMember) {
		return rooms.stream()
			.map(room -> RoomItemResponse.of(room, isMember))
			.toList();
	}
}
