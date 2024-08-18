package io.driver.codrive.modules.roomRequest.model.response;

import java.util.List;

import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomRequestListResponse(
	@Schema(description = "참여 요청 목록", examples = {"""
        [
            {
                "requestId": 1,
                "userId": 1,
                "nickname": "닉네임"
            }
        ]
"""})
	List<RequestDetailResponse> requests
) {
	public static RoomRequestListResponse of(List<RoomRequest> roomRequests) {
		return RoomRequestListResponse.builder()
			.requests(roomRequests.stream().map(RequestDetailResponse::of).toList())
			.build();
	}

	@Builder
	record RequestDetailResponse(
		@Schema(description = "참여 요청 ID", example = "1")
		Long requestId,

		@Schema(description = "참여 요청한 사용자 ID", example = "1")
		Long userId,

		@Schema(description = "참여 요청한 사용자 닉네임", example = "닉네임")
		String nickname
	) {
		public static RequestDetailResponse of(RoomRequest roomRequest) {
			return RequestDetailResponse.builder()
				.requestId(roomRequest.getRoomRequestId())
				.userId(roomRequest.getUser().getUserId())
				.nickname(roomRequest.getUser().getNickname())
				.build();
		}
	}
}
