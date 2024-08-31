package io.driver.codrive.modules.user.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserListResponse(
	@Schema(description = "사용자 목록")
	List<UserItemResponse> users
) {
	public static UserListResponse of(List<User> users, User currentUser) {
		return UserListResponse.builder()
			.users(UserItemResponse.of(users, currentUser))
			.build();
	}
}
