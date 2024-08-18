package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.mappings.roomUserMapping.model.LanguageMemberCountDto;
import io.driver.codrive.modules.room.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record JoinedRoomInfoResponse(
	@Schema(description = "그룹 ID", example = "1")
	Long roomId,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "그룹 제목", example = "그룹 제목")
	String title,

	@Schema(description = "현재 멤버 수", example = "15")
	int memberCount,

	@Schema(description = "모집 인원", example = "20")
	int capacity,

	@Schema(description = "그룹 상태", example = "활동 중")
	String roomStatus,

	@Schema(description = "사용 언어 별 인원", example = "[{\"language\": \"Java\", \"count\": 5}, {\"language\": \"Python\", \"count\": 10}]",
		implementation = LanguageMemberCountDto.class)
	List<LanguageMemberCountDto> languageMemberCount

) {
	public static JoinedRoomInfoResponse of(Room room, List<LanguageMemberCountDto> languageMemberCount) {
		return JoinedRoomInfoResponse.builder()
			.roomId(room.getRoomId())
			.imageSrc(room.getImageSrc())
			.tags(room.getLanguages())
			.title(room.getTitle())
			.memberCount(room.getMemberCount())
			.capacity(room.getCapacity())
			.roomStatus(room.getRoomStatus().name())
			.languageMemberCount(languageMemberCount)
			.build();
	}
}