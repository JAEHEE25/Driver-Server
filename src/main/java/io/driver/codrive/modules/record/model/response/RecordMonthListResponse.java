package io.driver.codrive.modules.record.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordMonthListResponse(
	@Schema(description = "풀이 목록 총 페이지 개수", examples = "5")
	int totalPage,

	@Schema(description = "사용자 정보", implementation = UserInfoResponse.class)
	UserInfoResponse user,

	@Schema(description = "문제 풀이 목록")
	List<RecordItemResponse> records

) {
	public static RecordMonthListResponse of(int totalPage, Page<Record> records, User user, Boolean isFollowing) {
		return RecordMonthListResponse.builder()
			.totalPage(totalPage)
			.user(UserInfoResponse.of(user, isFollowing))
			.records(RecordItemResponse.of(records))
			.build();
	}

	@Builder
	record UserInfoResponse(
		String nickname,
		Boolean isFollowing
	) {
		public static UserInfoResponse of(User user, Boolean isFollowing) {
			return UserInfoResponse.builder()
				.nickname(user.getNickname())
				.isFollowing(isFollowing)
				.build();
		}
	}
}

