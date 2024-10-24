package io.driver.codrive.modules.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.driver.codrive.global.discord.DiscordEventMessage;
import io.driver.codrive.global.discord.DiscordService;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.ForbiddenApplcationException;
import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.notification.service.NotificationDeleteService;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GithubRepositoryNameRequest;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.response.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private LanguageService languageService;

	@Mock
	private DiscordService discordService;

	@Mock
	private GithubCommitService githubCommitService;

	@Mock
	private NotificationDeleteService notificationDeleteService;

	private User mockUser;

	private User mockOtherUser;

	private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

	@BeforeEach
	void setUser() {
		mockUser = User.builder()
			.userId(1L)
			.name("name")
			.username("username")
			.nickname("nickname")
			.profileImg("PROFILE_URL")
			.comment("comment")
			.githubUrl("GITHUB_URL")
			.githubRepositoryName("REPOSITORY_NAME")
			.goal(0)
			.successRate(0)
			.solvedCount(0L)
			.withdraw(false)
			.language(Language.builder()
				.languageId(2L)
				.name("Java")
				.build())
			.followings(new ArrayList<>())
			.followers(new ArrayList<>())
			.roomUserMappings(new ArrayList<>())
			.records(new ArrayList<>())
			.createdRooms(new ArrayList<>())
			.roomRequests(new ArrayList<>())
			.build();
	}

	void setOtherUser() {
		mockOtherUser = User.builder()
			.userId(2L)
			.name("name_2")
			.username("username_2")
			.nickname("nickname_2")
			.profileImg("PROFILE_URL")
			.comment("comment")
			.githubUrl("GITHUB_URL")
			.githubRepositoryName("REPOSITORY_NAME")
			.goal(0)
			.successRate(0)
			.solvedCount(0L)
			.withdraw(false)
			.language(Language.builder()
				.languageId(3L)
				.name("Python")
				.build())
			.followings(new ArrayList<>())
			.followers(new ArrayList<>())
			.roomUserMappings(new ArrayList<>())
			.records(new ArrayList<>())
			.createdRooms(new ArrayList<>())
			.roomRequests(new ArrayList<>())
			.build();
	}

    void setSecurityContext(Long userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);
    }

    void tearDown() {
        mockedSecurityContextHolder.close();
    }

	@Test
	@DisplayName("userId로 사용자 조회 성공")
	void getUserById_success() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		//when
		User user = userService.getUserById(userId);

		//then
		assertNotNull(user);
		assertEquals(userId, user.getUserId());
	}

	@Test
	@DisplayName("userId로 사용자 조회 실패_사용자가 존재하지 않을 경우")
	void getUserById_fail_notFoundException() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		//when & then
		assertThrows(NotFoundApplcationException.class, () -> userService.getUserById(userId));
	}

	@Test
	@DisplayName("nickname으로 사용자 조회 성공")
	void getUserByNickname_success() {
		//given
		String nickname = "nickname";
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(mockUser));

		//when
		User user = userService.getUserByNickname(nickname);

		//then
		assertNotNull(user);
	}

	@Test
	@DisplayName("nickname으로 사용자 조회 실패_사용자가 존재하지 않을 경우")
	void getUserByNickname_fail_notFound() {
		//given
		String nickname = "nickname";
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

		//when & then
		NotFoundApplcationException notFoundApplcationException = assertThrows(NotFoundApplcationException.class,
			() -> userService.getUserByNickname(nickname));
		assertEquals("사용자을/를 찾을 수 없습니다.", notFoundApplcationException.getMessage());
	}

	@Test
	@DisplayName("사용자 정보 조회 실패_권한이 없을 경우")
	void getUserInfo_fail_forbidden() {
		//given
		setSecurityContext(1L);
		setOtherUser();
		when(userRepository.findById(mockOtherUser.getUserId())).thenReturn(Optional.of(mockOtherUser));

		//when & then
		assertThrows(ForbiddenApplcationException.class, () -> userService.getUserInfo(mockOtherUser.getUserId()));

		tearDown();
	}

	@Test
	@DisplayName("사용자 정보 조회 실패_사용자가 존재하지 않을 경우")
	void getUserInfo_fail_notFound() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		//when & then
		assertThrows(NotFoundApplcationException.class, () -> userService.getUserInfo(userId));
	}

	@Test
	@DisplayName("사용자 프로필 조회 성공_팔로우 중인 경우")
	void getProfile_success_follow() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		setOtherUser();
		when(userRepository.findById(mockOtherUser.getUserId())).thenReturn(Optional.of(mockOtherUser));

		Follow follow = Follow.builder() //현재 로그인한 유저(mockUser) -> 다른 유저(mockOtherUser) 팔로우
			.following(mockOtherUser)
			.follower(mockUser)
			.canceled(false)
			.build();
		mockUser.addFollowing(follow);

		//when
		UserProfileResponse response = userService.getProfile(mockOtherUser.getUserId());

		//then
		assertAll("UserProfileResponse",
			() -> assertNotNull(response),
			() -> assertEquals("Python", response.language()),
			() -> assertEquals(0, response.successRate()),
			() -> assertEquals("PROFILE_URL", response.profileImg()),
			() -> assertEquals("nickname_2", response.nickname()),
			() -> assertEquals("username_2", response.username()),
			() -> assertEquals("GITHUB_URL", response.githubUrl()),
			() -> assertEquals("comment", response.comment()),
			() -> assertTrue(response.isFollowing())
		);
		tearDown();
	}

	@Test
	@DisplayName("사용자 프로필 조회 성공_팔로우 중이 아닌 경우")
	void getProfile_success_not_follow() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		setOtherUser();
		when(userRepository.findById(mockOtherUser.getUserId())).thenReturn(Optional.of(mockOtherUser));

		//when
		UserProfileResponse response = userService.getProfile(mockOtherUser.getUserId());

		//then
		assertAll("UserProfileResponse",
			() -> assertNotNull(response),
			() -> assertEquals("Python", response.language()),
			() -> assertEquals(0, response.successRate()),
			() -> assertEquals("PROFILE_URL", response.profileImg()),
			() -> assertEquals("nickname_2", response.nickname()),
			() -> assertEquals("username_2", response.username()),
			() -> assertEquals("GITHUB_URL", response.githubUrl()),
			() -> assertEquals("comment", response.comment()),
			() -> assertFalse(response.isFollowing())
		);
		tearDown();
	}

	@Test
	@DisplayName("사용자 프로필 조회 성공_본인일 경우 null 반환")
	void getProfile_success_myself() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		//when
		UserProfileResponse response = userService.getProfile(userId);

		//then
		assertAll("UserProfileResponse",
			() -> assertNotNull(response),
			() -> assertEquals("Java", response.language()),
			() -> assertEquals(0, response.successRate()),
			() -> assertEquals("PROFILE_URL", response.profileImg()),
			() -> assertEquals("nickname", response.nickname()),
			() -> assertEquals("username", response.username()),
			() -> assertEquals("GITHUB_URL", response.githubUrl()),
			() -> assertEquals("comment", response.comment()),
			() -> assertNull(response.isFollowing())
		);
		tearDown();
	}

	@Test
	@DisplayName("닉네임 중복 검사 성공")
	void checkNicknameDuplication_success() {
		String nickname = "new_nickname";
		when(userRepository.existsByNickname(nickname)).thenReturn(false);
		NicknameRequest request = new NicknameRequest(nickname);

		assertDoesNotThrow(() -> userService.checkNicknameDuplication(request));
	}

	@Test
	@DisplayName("닉네임 중복 검사 실패_중복된 닉네임이 존재할 경우")
	void checkNicknameDuplication_fail_alreadyExist() {
		String newNickname = "new_nickname";
		when(userRepository.existsByNickname(newNickname)).thenReturn(true);
		NicknameRequest request = new NicknameRequest(newNickname);
		assertThrows(AlreadyExistsApplicationException.class, () -> userService.checkNicknameDuplication(request));
	}

	@Test
	@DisplayName("깃허브 레포지토리 이름 검사 성공")
	void checkGithubRepositoryName_success() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(githubCommitService.isExistRepository(mockUser, "REPOSITORY_NAME")).thenReturn(true);

		//when & then
		GithubRepositoryNameRequest request = new GithubRepositoryNameRequest("REPOSITORY_NAME");
		assertDoesNotThrow(() -> userService.checkGithubRepositoryName(request));

		tearDown();
	}

	@Test
	@DisplayName("깃허브 레포지토리 이름 검사 실패_레포지토리가 존재하지 않을 경우")
	void checkGithubRepositoryName_fail() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(githubCommitService.isExistRepository(mockUser, "REPOSITORY_NAME")).thenReturn(false);

		//when & then
		GithubRepositoryNameRequest request = new GithubRepositoryNameRequest("REPOSITORY_NAME");
		assertThrows(NotFoundApplcationException.class, () -> userService.checkGithubRepositoryName(request));

		tearDown();
	}

	@Test
	@DisplayName("현재 로그인한 사용자 프로필 변경 성공")
	void updateCurrentUserProfile_success() {
		//given
		setSecurityContext(1L);
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(languageService.getLanguageByName("Java")).thenReturn(Language.builder()
			.name("Java")
			.build());

		//when
		ProfileChangeRequest request = new ProfileChangeRequest("new_nickname", "Java",
			"new_comment", "NEW_GITHUB_URL", "NEW_REPOSITORY_NAME");
		ProfileChangeResponse response = userService.updateCurrentUserProfile(userId, request);

		//then
		assertAll("ProfileChangeResponse",
			() -> assertNotNull(response),
			() -> assertEquals("new_nickname", response.nickname()),
			() -> assertEquals("Java", response.language()),
			() -> assertEquals("new_comment", response.comment()),
			() -> assertEquals("NEW_GITHUB_URL", response.githubUrl()),
			() -> assertEquals("NEW_REPOSITORY_NAME", response.githubRepositoryName())
		);
		tearDown();
	}

	@Test
	@DisplayName("현재 로그인한 사용자 프로필 변경 실패_지원하지 않는 언어일 경우")
	void updateCurrentUserProfile_fail_unsupported_language() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(languageService.getLanguageByName("JAVA")).thenThrow(IllegalArgumentApplicationException.class);

		//when & then
		ProfileChangeRequest request = new ProfileChangeRequest("new_nickname", "JAVA",
			"new_comment", "NEW_GITHUB_URL", "NEW_REPOSITORY_NAME");
		assertThrows(IllegalArgumentApplicationException.class, () -> userService.updateCurrentUserProfile(userId, request));

		tearDown();
	}

	@Test
	@DisplayName("현재 로그인한 사용자 목표 설정 성공")
	void updateCurrentUserGoal() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		//when & then
		GoalChangeRequest request = new GoalChangeRequest(3);
		assertDoesNotThrow(() -> userService.updateCurrentUserGoal(userId, request));
		assertEquals(3, mockUser.getGoal());

		tearDown();
	}

	@Test
	@DisplayName("현재 로그인한 사용자 탈퇴 성공")
	void updateCurrentUserWithdraw_success() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		doNothing().when(discordService).sendMessage(DiscordEventMessage.LEAVE, mockUser.getNickname());

		//when & then
		assertDoesNotThrow(() -> userService.updateCurrentUserWithdraw(userId));

		tearDown();
	}

	@Test
	@DisplayName("팔로잉 목록 조회 성공")
	void getFollowings_success() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		setOtherUser();
		Follow follow = Follow.builder() //현재 로그인한 유저(mockUser) -> 다른 유저(mockOtherUser) 팔로우
			.following(mockOtherUser)
			.follower(mockUser)
			.canceled(false)
			.build();
		mockUser.addFollowing(follow);

		UserItemResponse userItemResponse = UserItemResponse.builder()
			.userId(mockOtherUser.getUserId())
			.nickname(mockOtherUser.getNickname())
			.profileImg(mockOtherUser.getProfileImg())
			.language(mockOtherUser.getLanguage().getName())
			.githubUrl(mockOtherUser.getGithubUrl())
			.isFollowing(true)
			.build();

		//when
		FollowListResponse response = userService.getFollowings();

		//then
		assertNotNull(response);
		assertEquals(1, response.count());
		assertEquals(List.of(userItemResponse), response.users());

		tearDown();
	}

	@Test
	@DisplayName("팔로워 목록 조회 성공_팔로우하지 않을 경우")
	void getFollowers_success_not_following() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		setOtherUser();
		Follow follow = Follow.builder() //다른 유저(mockOtherUser) -> 현재 로그인한 유저(mockUser) 팔로우
			.following(mockUser)
			.follower(mockOtherUser)
			.canceled(false)
			.build();
		mockUser.addFollower(follow);

		UserItemResponse userItemResponse = UserItemResponse.builder()
			.userId(mockOtherUser.getUserId())
			.nickname(mockOtherUser.getNickname())
			.profileImg(mockOtherUser.getProfileImg())
			.language(mockOtherUser.getLanguage().getName())
			.githubUrl(mockOtherUser.getGithubUrl())
			.isFollowing(false) //현재 로그인한 유저(mockUser) -> 다른 유저(mockOtherUser) 팔로우 X
			.build();

		//when
		FollowListResponse response = userService.getFollowers();

		//then
		assertNotNull(response);
		assertEquals(1, response.count());
		assertEquals(List.of(userItemResponse), response.users());

		tearDown();
	}

	@Test
	@DisplayName("팔로워 목록 조회 성공_팔로우할 경우")
	void getFollowers_success_following() {
		//given
		Long userId = 1L;
		setSecurityContext(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		setOtherUser();
		Follow follow_other_to_current = Follow.builder() //다른 유저(mockOtherUser) -> 현재 로그인한 유저(mockUser) 팔로우
			.following(mockUser)
			.follower(mockOtherUser)
			.canceled(false)
			.build();
		mockUser.addFollower(follow_other_to_current);

		Follow follow_current_to_other = Follow.builder() //현재 로그인한 유저(mockUser) -> 다른 유저(mockOtherUser) 팔로우
			.following(mockOtherUser)
			.follower(mockUser)
			.canceled(false)
			.build();
		mockUser.addFollowing(follow_current_to_other);

		UserItemResponse userItemResponse = UserItemResponse.builder()
			.userId(mockOtherUser.getUserId())
			.nickname(mockOtherUser.getNickname())
			.profileImg(mockOtherUser.getProfileImg())
			.language(mockOtherUser.getLanguage().getName())
			.githubUrl(mockOtherUser.getGithubUrl())
			.isFollowing(true) //현재 로그인한 유저(mockUser) -> 다른 유저(mockOtherUser) 팔로우 O
			.build();

		//when
		FollowListResponse response = userService.getFollowers();

		//then
		assertNotNull(response);
		assertEquals(1, response.count());
		assertEquals(List.of(userItemResponse), response.users());

		tearDown();
	}

    @Test
    @DisplayName("랜덤 사용자 목록 조회 성공")
    void getRandomUsersExcludingMeAndFollowings_Success() {
        // given
        User randomUser1 = User.builder()
			.userId(2L)
			.build();
        User randomUser2 = User.builder()
			.userId(3L)
			.build();
        List<User> randomUsers = List.of(randomUser1, randomUser2);
        when(userRepository.getRandomUsersExcludingMeAndFollowings(mockUser.getUserId())).thenReturn(randomUsers);

        // when
        List<User> result = userService.getRandomUsersExcludingMeAndFollowings(mockUser);

        // then
        assertNotNull(result);
        assertEquals(randomUsers.size(), result.size());
		assertEquals(randomUser1.getUserId(), result.get(0).getUserId());
		assertEquals(randomUser2.getUserId(), result.get(1).getUserId());
    }
}