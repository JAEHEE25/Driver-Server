package io.driver.codrive.modules.room.model.response;

import java.util.List;


import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RoomMembersResponse(
	@Schema(description = "그룹 멤버 목록 총 페이지 수", example = "1")
	int totalPage,

	@Schema(description = "그룹 멤버 목록")
	List<UserSummaryResponse> members
) {

	public static RoomMembersResponse of(int totalPage, List<User> members) {
		return RoomMembersResponse.builder()
			.totalPage(totalPage)
			.members(members.stream().map(UserSummaryResponse::of).toList())
			.build();
	}
}
