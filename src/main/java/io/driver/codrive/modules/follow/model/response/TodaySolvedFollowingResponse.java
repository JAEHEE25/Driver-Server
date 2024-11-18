package io.driver.codrive.modules.follow.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TodaySolvedFollowingResponse(
	@Schema(description = "오늘 문제를 푼 사용자 목록")
	List<TodaySolvedFollowingItemResponse> followings
) {
	public static TodaySolvedFollowingResponse of(List<User> followings) {
		return TodaySolvedFollowingResponse.builder()
			.followings(TodaySolvedFollowingItemResponse.of(followings))
			.build();
	}

	@Builder
	record TodaySolvedFollowingItemResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "프로필 이미지 URL", example = "IMAGE_URL")
		String profileImg,

		@Schema(description = "닉네임", example = "닉네임")
		String nickname
	) {
		public static List<TodaySolvedFollowingItemResponse> of(List<User> followings) {
			return followings.stream().map(TodaySolvedFollowingItemResponse::of).toList();
		}

		public static TodaySolvedFollowingItemResponse of(User user) {
			return TodaySolvedFollowingItemResponse.builder()
				.userId(user.getUserId())
				.profileImg(user.getProfileImg())
				.nickname(user.getNickname())
				.build();
		}
	}
}
