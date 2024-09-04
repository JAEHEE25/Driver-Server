package io.driver.codrive.modules.user.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FollowListResponse(
	@Schema(description = "팔로잉/팔로워 수", example = "1")
	int count,

	@Schema(description = "사용자 목록")
	List<UserItemResponse> users
) {
	public static FollowListResponse ofFollowings(List<User> followings) {
		return FollowListResponse.builder()
			.count(followings.size())
			.users(UserItemResponse.of(followings, true))
			.build();
	}

	public static FollowListResponse ofFollowers(List<User> followers, User currentUser) {
		return FollowListResponse.builder()
			.count(followers.size())
			.users(UserItemResponse.of(followers, currentUser))
			.build();
	}
}
