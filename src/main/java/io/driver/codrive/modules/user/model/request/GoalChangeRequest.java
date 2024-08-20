package io.driver.codrive.modules.user.model.request;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GoalChangeRequest(
	@Schema(description = "목표 문제 개수", example = "3")
	@Range(min = 1, max = 7, message = "목표 문제 개수는 {min}개 이상 {max}개 이하의 값이어야 합니다.")
	Integer goal
) {
	public static GoalChangeRequest of(Integer goal) {
		return GoalChangeRequest.builder()
			.goal(goal)
			.build();
	}
}
