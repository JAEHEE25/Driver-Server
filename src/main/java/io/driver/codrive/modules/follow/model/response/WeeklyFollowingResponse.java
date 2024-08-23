package io.driver.codrive.modules.follow.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record WeeklyFollowingResponse(
	@Schema(description = "팔로잉 목록")
	List<WeeklyFollowingItemResponse> followings
) {
	public static WeeklyFollowingResponse of(List<User> followings) {
		return WeeklyFollowingResponse.builder()
			.followings(WeeklyFollowingItemResponse.of(followings))
			.build();
	}

	@Builder
	record WeeklyFollowingItemResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "성과율", example = "30")
		int successRate,

		@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
		String profileImg,

		@Schema(description = "닉네임", example = "닉네임")
		String nickname,

		@Schema(description = "주 언어", example = "Java")
		String language
	) {
		public static List<WeeklyFollowingItemResponse> of(List<User> followings) {
			return followings.stream()
				.map(WeeklyFollowingItemResponse::of)
				.toList();
		}

		public static WeeklyFollowingItemResponse of(User following) {
			return WeeklyFollowingItemResponse.builder()
				.userId(following.getUserId())
				.successRate(following.getSuccessRate())
				.profileImg(following.getProfileImg())
				.nickname(following.getNickname())
				.language(following.getLanguage().getName())
				.build();
		}
	}
}
