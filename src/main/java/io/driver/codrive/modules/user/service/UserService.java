package io.driver.codrive.modules.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.response.ProfileChangeResponse;
import io.driver.codrive.modules.user.model.response.UserListResponse;
import io.driver.codrive.modules.user.model.response.UserDetailResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LanguageService languageService;
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

	public UserListResponse getRandomUsers() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> randomUsers = userRepository.getRandomUsersExceptMeAndFollowings(user.getUserId());
		return UserListResponse.of(randomUsers, user);
	}

	@Transactional
	public UserListResponse getFollowings() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followings = user.getFollowings().stream().map(Follow::getFollowing).toList();
		return UserListResponse.of(followings, user);
	}

	@Transactional
	public UserListResponse getFollowers() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		List<User> followers = user.getFollowers().stream().map(Follow::getFollower).toList();
		return UserListResponse.of(followers, user);
	}
}
