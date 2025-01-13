package io.driver.codrive.modules.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.discord.DiscordEventMessage;
import io.driver.codrive.global.discord.DiscordService;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.follow.domain.Follow;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LanguageService languageService;
	private final DiscordService discordService;
	private final GithubCommitService githubCommitService;
	private final NotificationDeleteService notificationDeleteService;
	private final UserRepository userRepository;

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundApplicationException("사용자"));
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> new NotFoundApplicationException("사용자"));
	}

	@PreAuthorize("@userAccessHandler.isOwner(#userId)")
	public UserDetailResponse getUserInfo(Long userId) {
		User user = getUserById(userId);
		return UserDetailResponse.of(user);
	}

	@Transactional(readOnly = true)
	public UserProfileResponse getProfile(Long currentUserId, Long userId) {
		User currentUser = getUserById(currentUserId);
		User user = getUserById(userId);
		Boolean isFollowing = currentUser.isFollowing(user);
		return UserProfileResponse.of(user, isFollowing);
	}

	public void checkNicknameDuplication(NicknameRequest request) {
		if (userRepository.existsByNickname(request.nickname())) {
			throw new AlreadyExistsApplicationException("닉네임");
		}
	}

	public void checkGithubRepositoryName(Long userId, GithubRepositoryNameRequest request) {
		boolean isExistRepository = githubCommitService.isExistRepository(userId, request.githubRepositoryName());
		if (!isExistRepository) {
			throw new NotFoundApplicationException("리포지토리");
		}
	}

	@Transactional
	@PreAuthorize("@userAccessHandler.isOwner(#userId)")
	public ProfileChangeResponse updateCurrentUserProfile(Long userId, ProfileChangeRequest request) {
		User user = getUserById(userId);
		user.changeNickname(request.nickname());
		user.changeLanguage(languageService.getLanguageByName(request.language()));
		user.changeComment(request.comment());
		user.changeGithubUrl(request.githubUrl());
		user.changeGithubRepositoryName(request.githubRepositoryName());
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	@PreAuthorize("@userAccessHandler.isOwner(#userId)")
	public UserGoalResponse updateCurrentUserGoal(Long userId, GoalChangeRequest request) {
		User user = getUserById(userId);
		user.changeGoal(request.goal());
		return UserGoalResponse.of(user.getGoal());
	}

	@Transactional
	@PreAuthorize("@userAccessHandler.isOwner(#userId)")
	public void updateCurrentUserWithdraw(Long userId) {
		User user = getUserById(userId);
		userRepository.delete(user);
		notificationDeleteService.deleteUserDataNotifications(user);
		discordService.sendMessage(DiscordEventMessage.LEAVE, user.getNickname());
	}

	@Transactional(readOnly = true)
	public FollowListResponse getFollowings(Long userId) {
		User user = getUserById(userId);
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).toList();
		return FollowListResponse.ofFollowings(followings);
	}

	@Transactional(readOnly = true)
	public FollowListResponse getFollowers(Long userId) {
		User user = getUserById(userId);
		List<User> followers = user.getFollowers().stream().map(Follow::getFollower).toList();
		return FollowListResponse.ofFollowers(followers, user);
	}

 	public List<User> getRandomUsersExcludingMeAndFollowings(Long userId){
		return userRepository.getRandomUsersExcludingMeAndFollowings(userId);
	}

	@Scheduled(cron = "0 0 0 * * MON") //매주 월요일 00:00:00에 실행
    @Transactional
    public void resetSuccessRate() {
        userRepository.resetSuccessRate();
    }
}
