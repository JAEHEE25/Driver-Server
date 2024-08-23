package io.driver.codrive.modules.follow.model.response;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FollowingSummaryListResponse(
	@Schema(description = "총 페이지 수", example = "1")
	int totalPage,

	@Schema(description = "팔로잉 목록")
	List<UserSummaryResponse> followings
) {
	public static FollowingSummaryListResponse of(int totalPage, List<User> followings) {
		return FollowingSummaryListResponse.builder()
			.totalPage(totalPage)
			.followings(UserSummaryResponse.of(followings))
			.build();
	}
}
