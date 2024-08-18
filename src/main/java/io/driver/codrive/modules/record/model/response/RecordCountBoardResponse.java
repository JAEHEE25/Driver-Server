package io.driver.codrive.modules.record.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordCountBoardResponse(
	@Schema(description = "날짜별 문제 풀이 개수", implementation = RecordCountResponse.class,
		example = "[{\"date\": \"1\", \"count\": 2}, {\"date\": \"2\", \"count\": 3}, {\"date\": \"3\", \"count\": 1}]")
	List<RecordCountResponse> board

) {
	public static RecordCountBoardResponse of(List<RecordCountResponse> recordCounts) {
		return RecordCountBoardResponse.builder()
			.board(recordCounts)
			.build();
	}

	@Builder
	public record RecordCountResponse (
		@Schema(description = "날짜", example = "30")
		String date,

		@Schema(description = "개수", example = "2")
		Long count
	) {
		public static RecordCountResponse of(String date, Long count) {
			return RecordCountResponse.builder()
				.date(date)
				.count(count)
				.build();
		}
	}
}
