package io.driver.codrive.modules.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.record.service.RecordService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserAchievementResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAchievementService {
	private final UserService userService;
	private final RecordService recordService;

	@Transactional(readOnly = true)
	public UserAchievementResponse getAchievement(Long userId) {
		User user = userService.getUserById(userId);
		int goal = user.getGoal();
		int todayCount = recordService.getTodayRecordCount(user);
		int successRate = user.getSuccessRate();
		int weeklyCount = recordService.getRecordsCountByWeek(user.getUserId(), LocalDate.now());
		int weeklyCountDifference = weeklyCount - getLastWeeklyCount(user.getUserId());
		return UserAchievementResponse.of(goal, todayCount, successRate, weeklyCount, weeklyCountDifference);
	}

	private int getLastWeeklyCount(Long userId) {
		LocalDate lastWeekDate = LocalDate.now().minusWeeks(1);
		return recordService.getRecordsCountByWeek(userId, lastWeekDate);
	}
}
