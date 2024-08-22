package io.driver.codrive.modules.follow.model.response;

import java.util.List;

import lombok.Builder;

@Builder
public record FollowingWeeklyCountResponse(
	List<WeeklyCountResponse> followings
) {
	public static FollowingWeeklyCountResponse of(List<WeeklyCountResponse> followings) {
		return FollowingWeeklyCountResponse.builder()
			.followings(followings)
			.build();
	}
	@Builder
	public record WeeklyCountResponse(
		String nickname,
		int count
	) {
		public static WeeklyCountResponse of(String nickname, int count) {
			return WeeklyCountResponse.builder()
				.nickname(nickname)
				.count(count)
				.build();
		}
	}
}
