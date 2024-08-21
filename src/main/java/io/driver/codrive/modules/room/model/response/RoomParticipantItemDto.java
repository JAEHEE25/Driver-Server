package io.driver.codrive.modules.room.model.response;

import java.time.LocalDateTime;

import io.driver.codrive.modules.roomRequest.domain.UserRequestStatus;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserSummaryResponse;
import lombok.Builder;

@Builder
public record RoomParticipantItemDto(
	UserSummaryResponse user,
	String status,
	LocalDateTime createdAt
) {
	public static RoomParticipantItemDto of(User user, UserRequestStatus status, LocalDateTime createdAt) {
		return RoomParticipantItemDto.builder()
			.user(UserSummaryResponse.of(user))
			.status(status.name())
			.createdAt(createdAt)
			.build();
	}
}