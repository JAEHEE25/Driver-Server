package io.driver.codrive.modules.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authorization.AuthorizationDeniedException;

import io.driver.codrive.annotation.WithTestUser;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.notification.service.NotificationDeleteService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.response.ProfileChangeResponse;
import io.driver.codrive.modules.user.model.response.UserDetailResponse;
import io.driver.codrive.modules.user.model.response.UserGoalResponse;

@SpringBootTest
public class UserServiceIntegrationTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private LanguageService languageService;


	@MockBean
	private NotificationDeleteService notificationDeleteService;

	private User currentUser;

	@BeforeEach
	void setUser() {
		currentUser = User.builder()
			.userId(1L)
			.name("name")
			.username("username")
			.nickname("nickname")
			.profileImg("PROFILE_URL")
			.comment("comment")
			.githubUrl("GITHUB_URL")
			.githubRepositoryName("repository")
			.language(Language.builder().languageId(1L).name("Python").build())
			.goal(2)
			.build();
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("사용자 정보 조회 성공")
	void getUserInfo_self_returnResponse() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		//when
		UserDetailResponse response = userService.getUserInfo(userId);

		//then
		assertNotNull(response);
		assertAll("UserDetailResponse",
			() -> assertEquals("name", response.name()),
			() -> assertEquals("nickname", response.nickname()),
			() -> assertEquals("PROFILE_URL", response.profileImg()),
			() -> assertEquals("comment", response.comment()),
			() -> assertEquals("GITHUB_URL", response.githubUrl()),
			() -> assertEquals("repository", response.githubRepositoryName()),
			() -> assertEquals("Python", response.language()),
			() -> assertEquals(2, response.goal())
		);
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("사용자 정보 조회 실패_다른 사용자의 정보를 요청할 경우")
	void getUserInfo_otherUser_throwAuthorizationDeniedException() {
		//given
		Long userId = 2L;

		//when & then
		assertThrows(AuthorizationDeniedException.class, () -> userService.getUserInfo(userId));
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("프로필 변경 성공")
	void updateCurrentUserProfile_self_returnResponse() {
		//given
		Long userId = 1L;
		String newLanguage = "Java";
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
		when(languageService.getLanguageByName(newLanguage)).thenReturn(Language.builder().name(newLanguage).build());

		//when
		ProfileChangeRequest request = new ProfileChangeRequest("newNickname", newLanguage,
			"newComment", "NEW_GITHUB_URL", "newRepository");
		ProfileChangeResponse response = userService.updateCurrentUserProfile(userId, request);

		//then
		assertNotNull(response);
		assertAll("ProfileChangeResponse",
			() -> assertEquals("newNickname", response.nickname()),
			() -> assertEquals(newLanguage, response.language()),
			() -> assertEquals("newComment", response.comment()),
			() -> assertEquals("NEW_GITHUB_URL", response.githubUrl()),
			() -> assertEquals("newRepository", response.githubRepositoryName())
		);
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("프로필 변경 실패_다른 사용자의 프로필 변경을 요청했을 경우")
	void updateCurrentUserProfile_otherUser_throwAuthorizationDeniedException() {
		//given
		Long userId = 2L;

		//when & then
		ProfileChangeRequest request = new ProfileChangeRequest("newNickname", "Java",
			"newComment", "NEW_GITHUB_URL", "newRepository");
		assertThrows(AuthorizationDeniedException.class, () -> userService.updateCurrentUserProfile(userId, request));
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("목표 변경 성공")
	void updateCurrentUserGoal_self_returnResponse() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		//when
		GoalChangeRequest request = new GoalChangeRequest(5);
		UserGoalResponse response = userService.updateCurrentUserGoal(userId, request);

		//then
		assertNotNull(response);
		assertAll("UserGoalResponse",
			() -> assertEquals(5, response.goal())
		);
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("목표 변경 실패_다른 사용자의 목표 변경을 요청했을 경우")
	void updateCurrentUserGoal_otherUser_throwAuthorizationDeniedException() {
		//given
		Long userId = 2L;

		//when & then
		GoalChangeRequest request = new GoalChangeRequest(5);
		assertThrows(AuthorizationDeniedException.class, () -> userService.updateCurrentUserGoal(userId, request));
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("탈퇴 성공")
	void updateCurrentUserWithdraw_self_returnResponse() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
		doNothing().when(userRepository).delete(currentUser);
		doNothing().when(notificationDeleteService).deleteUserDataNotifications(currentUser);

		//when
		assertDoesNotThrow(() -> userService.updateCurrentUserWithdraw(userId));
	}

	@Test
	@WithTestUser(userId = 1L, token = "TOKEN")
	@DisplayName("탈퇴 실패_다른 사용자의 탈퇴를 요청했을 경우")
	void updateCurrentUserWithdraw_otherUser_throwAuthorizationDeniedException() {
		//given
		Long userId = 2L;

		//when & then
		assertThrows(AuthorizationDeniedException.class, () -> userService.updateCurrentUserWithdraw(userId));
	}

}
