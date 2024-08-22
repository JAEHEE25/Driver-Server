package io.driver.codrive.modules.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserAchievementResponse(
	@Schema(description = "사용자가 설정한 목표", example = "3")
	int goal,
	@Schema(description = "오늘의 문제 풀이 개수", example = "1")
	int todayCount,
	@Schema(description = "성과율", example = "30")
	int successRate,
	@Schema(description = "이번 주 기준 지난 주와의 문제 풀이 개수 차이 (이번 주 문제 풀이 개수 - 지난 주 문제 풀이 개수)", example = "1")
	int weeklyCountDifference
) {
	public static UserAchievementResponse of(int goal, int todayCount, int successRate, int weeklyCountDifference) {
		return UserAchievementResponse.builder()
			.goal(goal)
			.todayCount(todayCount)
			.successRate(successRate)
			.weeklyCountDifference(weeklyCountDifference)
			.build();
	}
}
