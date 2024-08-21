package io.driver.codrive.modules.record.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BoardResponse(
	@Schema(description = "총 문제 풀이 개수", example = "30")
	int totalCount,

	@Schema(description = "최장 문제 풀이 기간", example = "3")
	int longestPeriod,

	@Schema(description = "최대 문제 풀이 개수", example = "5")
	int maxCount,

	@Schema(description = "날짜별 문제 풀이 여부")
	List<RecordSolvedResponse> board

) {
	public static BoardResponse of(int totalCount, int longestPeriod, int maxCount, List<RecordSolvedResponse> recordCounts) {
		return BoardResponse.builder()
			.totalCount(totalCount)
			.longestPeriod(longestPeriod)
			.maxCount(maxCount)
			.board(recordCounts)
			.build();
	}

	@Builder
	public record RecordSolvedResponse(
		@Schema(description = "날짜", example = "30")
		String date,

		@Schema(description = "문제 풀이 여부", example = "true")
		boolean isSolved
	) {
		public static RecordSolvedResponse of(String date, Long count) {
			boolean isSolved = count > 0;
			return RecordSolvedResponse.builder()
				.date(date)
				.isSolved(isSolved)
				.build();
		}
	}
}
