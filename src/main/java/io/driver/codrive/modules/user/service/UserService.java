package io.driver.codrive.modules.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.global.util.CalculateUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.response.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LanguageService languageService;
	private final UserRepository userRepository;
	private final RecordRepository recordRepository;

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

	public void checkNicknameDuplication(NicknameRequest request) {
		if (userRepository.existsByNickname(request.nickname())) {
			throw new AlreadyExistsApplicationException("닉네임");
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
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	public void updateCurrentUserGoal(Long userId, GoalChangeRequest request) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		user.changeGoal(request.goal());
	}

	@Transactional
	public void updateCurrentUserWithdraw(Long userId) {
		User user = getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		userRepository.delete(user);
	}

	@Transactional
	public FollowListResponse getFollowings() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).toList();
		return FollowListResponse.ofFollowings(followings);
	}

	@Transactional
	public FollowListResponse getFollowers() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followers = user.getFollowers().stream().map(Follow::getFollower).toList();
		return FollowListResponse.ofFollowers(followers, user);
	}

	@Transactional
	public List<User> getRandomUsersExcludingMeAndFollowings(User user){
		return userRepository.getRandomUsersExcludingMeAndFollowings(user.getUserId());
	}

	@Transactional
	public void updateSuccessRate(User user) {
		LocalDate pivotDate = LocalDate.now();
		int solvedDayCountByWeek = recordRepository.getSolvedDaysByWeek(user.getUserId(), pivotDate);
		System.out.println("solvedDayCountByWeek: " + solvedDayCountByWeek);
		int successRate = CalculateUtils.calculateSuccessRate(solvedDayCountByWeek);
		user.changeSuccessRate(successRate);
	}

	@Transactional
	public int getThisWeekRecordsCount(User user) {
		LocalDate pivotDate = LocalDate.now();
		return recordRepository.getRecordCountByWeek(user.getUserId(), pivotDate);
	}

	@Transactional
	public int getTodayRecordCount(User user) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); //오늘 00:00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59); //오늘 23:59:59
		return recordRepository.findAllByUserAndCreatedAtBetween(user, startOfDay, endOfDay).size();
	}

	@Transactional
	public UserAchievementResponse getAchievement() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		int goal = user.getGoal();
		int todayCount = getTodayRecordCount(user);
		int successRate = user.getSuccessRate();
		int weeklyCountDifference = getWeeklyCountDifference(user);
		return UserAchievementResponse.of(goal, todayCount, successRate, weeklyCountDifference);
	}

	@Transactional
	protected int getWeeklyCountDifference(User user) {
		int weeklyCount = getThisWeekRecordsCount(user);
		LocalDate pivotDate = LocalDate.now().minusWeeks(1);
		int lastWeeklyCount = recordRepository.getRecordCountByWeek(user.getUserId(), pivotDate);
		return weeklyCount - lastWeeklyCount;
	}

	@Transactional
	public UserProfileResponse getProfile(Long userId) {
		User user = getUserById(userId);
		User currentUser = getUserById(AuthUtils.getCurrentUserId());
		Boolean isFollowing = currentUser.isFollowing(user);
		return UserProfileResponse.of(user, isFollowing);
	}
}
