package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
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
	public static RoomParticipantListResponse of(int totalPage, List<RoomRequest> participants) {
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
		public static List<RoomParticipantItemResponse> of(List<RoomRequest> requests) {
			return requests.stream().map(RoomParticipantItemResponse::of).toList();
		}
		public static RoomParticipantItemResponse of(RoomRequest request) {
			return RoomParticipantItemResponse.builder()
				.user(UserSummaryResponse.of(request.getUser()))
				.status(request.getUserRequestStatus().name())
				.build();
		}
	}
}

