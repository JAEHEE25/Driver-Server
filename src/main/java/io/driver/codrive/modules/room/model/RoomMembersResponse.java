package io.driver.codrive.modules.room.model;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import lombok.Builder;

@Builder
public record RoomMembersResponse(
	List<MemberListResponse> members
) {

	public static RoomMembersResponse of(List<User> users) {
		return RoomMembersResponse.builder()
				.members(users.stream().map(MemberListResponse::of).toList())
				.build();
	}

	@Builder
	record MemberListResponse(
		Long userId,
		String nickname,
		String profileImageUrl
	) {
		public static MemberListResponse of(User user) {
			return MemberListResponse.builder()
					.userId(user.getUserId())
					.nickname(user.getNickname())
					.profileImageUrl(user.getProfileUrl())
					.build();
		}
	}
}
