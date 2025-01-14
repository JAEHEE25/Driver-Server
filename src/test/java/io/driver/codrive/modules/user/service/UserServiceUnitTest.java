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
import org.mockito.junit.jupiter.MockitoExtension;

import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GithubRepositoryNameRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.response.FollowListResponse;
import io.driver.codrive.modules.user.model.response.UserProfileResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private GithubCommitService githubCommitService;

	private User currentUser;

	private User otherUser;

	@BeforeEach
	void setUser() {
		currentUser = User.builder()
			.userId(1L)
			.username("username")
			.nickname("nickname")
			.language(Language.builder().languageId(1L).name("Python").build())
			.successRate(50)
			.profileImg("PROFILE_URL")
			.githubUrl("GITHUB_URL")
			.comment("comment")
			.followings(new ArrayList<>())
			.followers(new ArrayList<>())
			.build();
	}

	void setOtherUser() {
		otherUser = User.builder()
			.userId(2L)
			.username("username2")
			.nickname("nickname2")
			.language(Language.builder().languageId(2L).name("Java").build())
			.successRate(100)
			.profileImg("PROFILE_URL2")
			.githubUrl("GITHUB_URL2")
			.comment("comment2")
			.build();
	}

	@Test
	@DisplayName("userId로 User 조회 성공")
	void getUserById_userExists_returnUser() {
		//given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		//when
		User serviceUser = userService.getUserById(userId);

		//then
		assertNotNull(serviceUser);
		assertEquals(currentUser.getUserId(), serviceUser.getUserId());
		verify(userRepository).findById(userId);
	}

	@Test
	@DisplayName("userId로 User 조회 실패_사용자가 존재하지 않을 경우")
	void getUserById_userDoesNotExist_throwNotFoundApplicationException() {
		// given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(NotFoundApplicationException.class, () -> userService.getUserById(userId));
	}

	@Test
	@DisplayName("nickname으로 User 조회 성공")
	void getUserByNickname_userExists_returnUser() {
		//given
		String nickname = "nickname";
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(currentUser));

		//when
		User serviceUser = userService.getUserByNickname(nickname);

		//then
		assertNotNull(serviceUser);
		assertEquals(currentUser.getNickname(), serviceUser.getNickname());
		verify(userRepository).findByNickname(nickname);
	}

	@Test
	@DisplayName("nickname으로 User 조회 실패_사용자가 존재하지 않을 경우")
	void getUserByNickname_userDoesNotExist_throwNotFoundApplicationException() {
		// given
		String nickname = "nickname";
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

		// when & then
		assertThrows(NotFoundApplicationException.class, () -> userService.getUserByNickname(nickname));
	}

	@Test
	@DisplayName("프로필 조회 성공_상대방을 팔로우 중일 경우")
	void getProfile_following_returnUserProfileResponse() {
		//given
		setOtherUser();
		Long currentUserId = 1L;
		Long otherUserId = 2L;
		when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
		when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

		//currentUser -> otherUser 팔로우
		Follow follow = Follow.builder()
			.following(otherUser)
			.follower(currentUser)
			.build();
		currentUser.getFollowings().add(follow);

		//when
		UserProfileResponse response = userService.getProfile(currentUserId, otherUserId);

		//then
		assertNotNull(response);
		assertAll("UserProfileResponse",
			() -> assertEquals("Java", response.language()),
			() -> assertEquals(100, response.successRate()),
			() -> assertEquals("PROFILE_URL2", response.profileImg()),
			() -> assertEquals("nickname2", response.nickname()),
			() -> assertEquals("username2", response.username()),
			() -> assertEquals("GITHUB_URL2", response.githubUrl()),
			() -> assertEquals("comment2", response.comment()),
			() -> assertTrue(response.isFollowing())
		);
	}

	@Test
	@DisplayName("프로필 조회 성공_상대방을 팔로우하고 있지 않을 경우")
	void getProfile_notfollowing_returnUserProfileResponse() {
		//given
		setOtherUser();
		Long currentUserId = 1L;
		Long otherUserId = 2L;
		when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
		when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

		//when
		UserProfileResponse response = userService.getProfile(currentUserId, otherUserId);

		//then
		assertNotNull(response);
		assertAll("UserProfileResponse",
			() -> assertNotNull(response),
			() -> assertEquals("Java", response.language()),
			() -> assertEquals(100, response.successRate()),
			() -> assertEquals("PROFILE_URL2", response.profileImg()),
			() -> assertEquals("nickname2", response.nickname()),
			() -> assertEquals("username2", response.username()),
			() -> assertEquals("GITHUB_URL2", response.githubUrl()),
			() -> assertEquals("comment2", response.comment()),
			() -> assertFalse(response.isFollowing())
		);
	}

	@Test
	@DisplayName("프로필 조회 성공_자기 자신에 대한 프로필을 조회할 경우")
	void getProfile_self_returnUserProfileResponse() {
		//given
		Long currentUserId = 1L;
		when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

		//when
		UserProfileResponse response = userService.getProfile(currentUserId, currentUserId);

		//then
		assertNotNull(response);
		assertAll("UserProfileResponse",
			() -> assertNotNull(response),
			() -> assertEquals("Python", response.language()),
			() -> assertEquals(50, response.successRate()),
			() -> assertEquals("PROFILE_URL", response.profileImg()),
			() -> assertEquals("nickname", response.nickname()),
			() -> assertEquals("username", response.username()),
			() -> assertEquals("GITHUB_URL", response.githubUrl()),
			() -> assertEquals("comment", response.comment()),
			() -> assertNull(response.isFollowing())
		);
	}

	@Test
	@DisplayName("닉네임 중복 검사 성공_중복된 닉네임 없음")
	void checkNicknameDuplication_notDuplicate() {
		//given
		String nickname = "nickname";
		when(userRepository.existsByNickname(nickname)).thenReturn(false);

		//when & then
		NicknameRequest request = new NicknameRequest(nickname);
		assertDoesNotThrow(() -> userService.checkNicknameDuplication(request));
	}

	@Test
	@DisplayName("닉네임 중복 검사 실패_중복된 닉네임 있음")
	void checkNicknameDuplication_duplicate_throwAlreadyExistsApplicationException() {
		//given
		String nickname = "nickname";
		when(userRepository.existsByNickname(nickname)).thenReturn(true);

		//when & then
		NicknameRequest request = new NicknameRequest(nickname);
		assertThrows(AlreadyExistsApplicationException.class, () -> userService.checkNicknameDuplication(request));
	}


	@Test
	@DisplayName("Github 리포지토리 이름 검사_실제 존재하는 리포지토리 이름일 경우")
	void checkGithubRepositoryName_repositoryNameExists() {
		//given
		Long userId = 1L;
		GithubRepositoryNameRequest request = new GithubRepositoryNameRequest("repository");
		when(githubCommitService.isExistRepository(userId, request.githubRepositoryName())).thenReturn(true);

		//when & then
		assertDoesNotThrow(() -> userService.checkGithubRepositoryName(userId, request));
	}

	@Test
	@DisplayName("Github 리포지토리 이름 검사_실제 존재하는 리포지토리 이름일 경우")
	void checkGithubRepositoryName_repositoryNameDoesNotExist_throwNotFoundApplicationException() {
		//given
		Long userId = 1L;
		GithubRepositoryNameRequest request = new GithubRepositoryNameRequest("repository");
		when(githubCommitService.isExistRepository(userId, request.githubRepositoryName())).thenReturn(false);

		//when & then
		assertThrows(NotFoundApplicationException.class, () -> userService.checkGithubRepositoryName(userId, request));
	}

	@Test
	@DisplayName("팔로잉 목록 조회 성공")
	void getFollowings() {
		//given
		setOtherUser();
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		//currentUser -> otherUser 팔로우
		Follow follow = Follow.builder()
			.following(otherUser)
			.follower(currentUser)
			.build();
		currentUser.getFollowings().add(follow);

		//when
		FollowListResponse response = userService.getFollowings(userId);

		//then
		assertNotNull(response);
		assertAll("FollowListResponse",
			() -> assertNotNull(response),
			() -> assertEquals(1, response.count())
		);
	}

	@Test
	@DisplayName("팔로워 목록 조회 성공")
	void getFollowers() {
		//given
		setOtherUser();
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		//otherUser -> currentUser 팔로우
		Follow follow = Follow.builder()
			.following(currentUser)
			.follower(otherUser)
			.build();
		currentUser.getFollowers().add(follow);

		//when
		FollowListResponse response = userService.getFollowers(userId);

		//then
		assertNotNull(response);
		assertAll("FollowListResponse",
			() -> assertNotNull(response),
			() -> assertEquals(1, response.count())
		);
	}

	@Test
	@DisplayName("추천 사용자 목록 조회 성공")
	void getRandomUsersExcludingMeAndFollowings() {
		//given
		setOtherUser();
		Long userId = 1L;
		List<User> randomUsers = new ArrayList<>();
		randomUsers.add(otherUser);
		when(userRepository.getRandomUsersExcludingMeAndFollowings(userId)).thenReturn(randomUsers);

		//when
		List<User> result = userService.getRandomUsersExcludingMeAndFollowings(userId);

		//then
		assertNotNull(result);
		assertEquals(randomUsers, result);
	}

	@Test
	@DisplayName("성과율 초기화 호출 검증")
	void resetSuccessRate() {
		// when
        userService.resetSuccessRate();

        // then
        verify(userRepository, times(1)).resetSuccessRate();
	}
}