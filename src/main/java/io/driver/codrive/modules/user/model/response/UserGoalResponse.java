package io.driver.codrive.modules.user.model.response;

import lombok.Builder;

@Builder
public record UserGoalResponse(
	int goal
) {
	public static UserGoalResponse of(int goal) {
		return UserGoalResponse.builder()
			.goal(goal)
			.build();
	}
}
