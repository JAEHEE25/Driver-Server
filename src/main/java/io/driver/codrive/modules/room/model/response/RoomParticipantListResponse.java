package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.user.model.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomParticipantListResponse(
	@Schema(description = "총 페이지 수", example = "1")
	int totalPage,

	@Schema(description = "참여 요청 및 멤버 목록")
	List<RoomParticipantItemResponse> participants
) {
	public static RoomParticipantListResponse of(int totalPage, List<RoomParticipantItemDto> participants) {
		return RoomParticipantListResponse.builder()
			.totalPage(totalPage)
			.participants(RoomParticipantItemResponse.of(participants))
			.build();
	}

	@Builder
	record RoomParticipantItemResponse(
		UserSummaryResponse user,
		String status
	) {
		public static List<RoomParticipantItemResponse> of(List<RoomParticipantItemDto> participants) {
			return participants.stream().map(RoomParticipantItemResponse::of).toList();
		}
		public static RoomParticipantItemResponse of(RoomParticipantItemDto dto) {
			return RoomParticipantItemResponse.builder()
				.user(dto.user())
				.status(dto.status())
				.build();
		}
	}
}

