package io.driver.codrive.modules.roomRequest.model.response;

import java.util.List;

import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomRequestListResponse(
	@Schema(description = "승인된 참여 요청 수", examples = "10")
	int approvedCount,

	@Schema(description = "참여 요청 목록")
	List<RequestDetailResponse> requests
) {
	public static RoomRequestListResponse of(int approvedCount, List<RoomRequest> roomRequests) {
		return RoomRequestListResponse.builder()
			.approvedCount(approvedCount)
			.requests(roomRequests.stream().map(RequestDetailResponse::of).toList())
			.build();
	}

	@Builder
	record RequestDetailResponse(
		@Schema(description = "참여 요청 ID", example = "1")
		Long roomRequestId,

		@Schema(description = "참여 요청한 사용자 언어", example = "Java")
		String language,

		@Schema(description = "참여 요청한 사용자 닉네임", example = "닉네임")
		String nickname,

		@Schema(description = "참여 요청 상태", example = "APPROVED", allowableValues = {"WAITING", "REQUESTED", "JOINED"})
		String roomRequestStatus
	) {
		public static RequestDetailResponse of(RoomRequest roomRequest) {
			return RequestDetailResponse.builder()
				.roomRequestId(roomRequest.getRoomRequestId())
				.language(roomRequest.getUser().getLanguage().getName())
				.nickname(roomRequest.getUser().getNickname())
				.roomRequestStatus(roomRequest.getUserRequestStatus().name())
				.build();
		}
	}
}
