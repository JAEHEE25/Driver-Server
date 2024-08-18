package io.driver.codrive.modules.room.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomMembersResponse(
	@Schema(description = "그룹 멤버 목록", examples = {"""
		[
			{
				"userId": 1,
				"nickname": "닉네임",
				"profileImg": "IMAGE_URL"
			}
		]
		"""
	})
	List<MemberListResponse> members
) {

	public static RoomMembersResponse of(List<User> users) {
		return RoomMembersResponse.builder()
				.members(users.stream().map(MemberListResponse::of).toList())
				.build();
	}

	@Builder
	record MemberListResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "사용자 닉네임", example = "닉네임")
		String nickname,

		@Schema(description = "사용자 프로필 이미지 URL", example = "IMAGE_URL")
		String profileImg
	) {
		public static MemberListResponse of(User user) {
			return MemberListResponse.builder()
					.userId(user.getUserId())
					.nickname(user.getNickname())
					.profileImg(user.getProfileImg())
					.build();
		}
	}
}
