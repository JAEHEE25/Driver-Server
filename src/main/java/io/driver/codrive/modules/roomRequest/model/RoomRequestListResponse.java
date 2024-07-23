package io.driver.codrive.modules.roomRequest.model;

import java.util.List;

import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import lombok.Builder;

@Builder
public record RoomRequestListResponse(
	List<RequestDetailResponse> requests
) {
	public static RoomRequestListResponse of(List<RoomRequest> roomRequests) {
		return RoomRequestListResponse.builder()
			.requests(roomRequests.stream().map(RequestDetailResponse::of).toList())
			.build();
	}

	@Builder
	record RequestDetailResponse(
		Long requestId,
		Long userId,
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
