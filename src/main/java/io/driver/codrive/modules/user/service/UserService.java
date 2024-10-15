package io.driver.codrive.modules.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.discord.DiscordEventMessage;
import io.driver.codrive.global.discord.DiscordService;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GithubRepositoryNameRequest;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.response.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LanguageService languageService;
	private final DiscordService discordService;
	private final GithubCommitService githubCommitService;
	private final UserRepository userRepository;

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundApplcationException("사용자"));
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> new NotFoundApplcationException("사용자"));
	}

	public UserDetailResponse getUserInfo(Long userId) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		return UserDetailResponse.of(user);
	}

	@Transactional(readOnly = true)
	public UserProfileResponse getProfile(Long userId) {
		User user = getUserById(userId);
		User currentUser = getUserById(AuthUtils.getCurrentUserId());
		Boolean isFollowing = currentUser.isFollowing(user);
		return UserProfileResponse.of(user, isFollowing);
	}

	public void checkNicknameDuplication(NicknameRequest request) {
		if (userRepository.existsByNickname(request.nickname())) {
			throw new AlreadyExistsApplicationException("닉네임");
		}
	}

	public void checkGithubRepositoryName(GithubRepositoryNameRequest request) {
		User user = getUserById(AuthUtils.getCurrentUserId());
		boolean isExistRepository = githubCommitService.isExistRepository(user, request.githubRepositoryName());
		if (!isExistRepository) {
			throw new NotFoundApplcationException("레포지토리");
		}
	}

	@Transactional
	public ProfileChangeResponse updateCurrentUserProfile(Long userId, ProfileChangeRequest request) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		user.changeNickname(request.nickname());
		user.changeLanguage(languageService.getLanguageByName(request.language()));
		user.changeComment(request.comment());
		user.changeGithubUrl(request.githubUrl());
		user.changeGithubRepositoryName(request.githubRepositoryName());
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	public UserGoalResponse updateCurrentUserGoal(Long userId, GoalChangeRequest request) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		user.changeGoal(request.goal());
		return UserGoalResponse.of(user.getGoal());
	}

	@Transactional
	public void updateCurrentUserWithdraw(Long userId) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		updateJoinedRoomsMemberCount(user);
		updateRequestedRoomsRequestedCount(user);
		discordService.sendMessage(DiscordEventMessage.LEAVE, user.getNickname());
		userRepository.delete(user);
	}

	@Transactional
	protected void updateJoinedRoomsMemberCount(User user) {
		user.getJoinedRooms().forEach(room -> room.changeMemberCount(room.getMemberCount() - 1));
	}

	@Transactional
	protected void updateRequestedRoomsRequestedCount(User user) {
		user.getRoomRequests().forEach(roomRequest -> {
			Room room = roomRequest.getRoom();
			room.changeRequestedCount(room.getRequestedCount() - 1);
		});
	}

	@Transactional(readOnly = true)
	public FollowListResponse getFollowings() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).toList();
		return FollowListResponse.ofFollowings(followings);
	}

	@Transactional(readOnly = true)
	public FollowListResponse getFollowers() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followers = user.getFollowers().stream().map(Follow::getFollower).toList();
		return FollowListResponse.ofFollowers(followers, user);
	}

	@Transactional(readOnly = true)
	public List<User> getRandomUsersExcludingMeAndFollowings(User user){
		return userRepository.getRandomUsersExcludingMeAndFollowings(user.getUserId());
	}

	@Scheduled(cron = "0 0 0 * * MON") //매주 월요일 00:00:00에 실행
    @Transactional
    public void resetSuccessRate() {
        userRepository.resetSuccessRate();
    }
}
