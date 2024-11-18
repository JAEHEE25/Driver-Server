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

	@Schema(description = "그룹 UUID", example = "12345678")
	String uuid,

	@Schema(description = "그룹 대표 이미지 URL", example = "IMAGE_URL")
	String imageSrc,

	@Schema(description = "언어 태그", example = "[\"Java\", \"Python\"]")
	List<String> tags,

	@Schema(description = "그룹 제목", example = "그룹 제목")
	String title,

	@Schema(description = "그룹 비밀번호 (그룹장이 아닐 경우 null)", example = "비밀번호")
	String password,

	@Schema(description = "현재 멤버 수", example = "15")
	int memberCount,

	@Schema(description = "모집 인원", example = "20")
	int capacity,

	@Schema(description = "승인된 참여 요청 수", example = "10")
	int approvedCount,

	@Schema(description = "신청 인원", example = "30")
	int requestedCount,

	@Schema(description = "그룹 상태", example = "ACTIVE", allowableValues = {"CLOSED", "ACTIVE", "INACTIVE"})
	String roomStatus,

	@Schema(description = "사용 언어 별 인원")
	List<LanguageMemberCountDto> languageMemberCount,

	@Schema(description = "공개 그룹인지 여부", example = "true")
	boolean isPublicRoom

) {
	public static JoinedRoomInfoResponse of(Room room, String password, int approvedCount, int requestedCount, List<LanguageMemberCountDto> languageMemberCount) {
		return JoinedRoomInfoResponse.builder()
			.roomId(room.getRoomId())
			.uuid(room.getUuid())
			.imageSrc(room.getImageSrc())
			.tags(room.getLanguages())
			.title(room.getTitle())
			.password(password)
			.memberCount(room.getMemberCount())
			.capacity(room.getCapacity())
			.approvedCount(approvedCount)
			.requestedCount(requestedCount)
			.roomStatus(room.getRoomStatus().name())
			.languageMemberCount(languageMemberCount)
			.isPublicRoom(room.isPublicRoom())
			.build();
	}
}