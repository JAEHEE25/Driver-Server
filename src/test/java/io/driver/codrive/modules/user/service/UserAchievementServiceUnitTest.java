package io.driver.codrive.modules.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.driver.codrive.modules.record.service.RecordService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.model.response.UserAchievementResponse;

@ExtendWith(MockitoExtension.class)
class UserAchievementServiceUnitTest {

	@InjectMocks
	private UserAchievementService userAchievementService;

	@Mock
	private UserService userService;

	@Mock
	private RecordService recordService;

	private User currentUser;

	@BeforeEach
	void setUser() {
		currentUser = User.builder()
			.userId(1L)
			.goal(2)
			.successRate(100)
			.build();
	}

	@Test
	@DisplayName("사용자 성과 조회 성공")
	void getAchievement() {
		//given
		Long userId = 1L;
		when(userService.getUserById(userId)).thenReturn(currentUser);
		when(recordService.getTodayRecordCount(currentUser)).thenReturn(3);
		when(recordService.getRecordsCountByWeek(userId, LocalDate.now())).thenReturn(5);

		//when
		UserAchievementResponse response = userAchievementService.getAchievement(userId);

		//then
		assertNotNull(response);
		assertAll("UserAchievementResponse",
			() -> assertEquals(2, response.goal()),
			() -> assertEquals(3, response.todayCount()),
			() -> assertEquals(100, response.successRate()),
			() -> assertEquals(5, response.weeklyCount()),
			() -> assertEquals(5, response.weeklyCountDifference())
		);
	}

}