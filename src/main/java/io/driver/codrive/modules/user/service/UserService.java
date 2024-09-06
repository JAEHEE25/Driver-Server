package io.driver.codrive.modules.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.room.domain.Room;
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
		updateJoinedRoomsMemberCount(user);
		updateRequestedRoomsRequestedCount(user);
		userRepository.delete(user);
	}

	private void updateJoinedRoomsMemberCount(User user) {
		user.getJoinedRooms().forEach(room -> room.changeMemberCount(room.getMemberCount() - 1));
	}

	private void updateRequestedRoomsRequestedCount(User user) {
		user.getRoomRequests().forEach(roomRequest -> {
			Room room = roomRequest.getRoom();
			room.changeRequestedCount(room.getRequestedCount() - 1);
		});
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
	public UserProfileResponse getProfile(Long userId) {
		User user = getUserById(userId);
		User currentUser = getUserById(AuthUtils.getCurrentUserId());
		Boolean isFollowing = currentUser.isFollowing(user);
		return UserProfileResponse.of(user, isFollowing);
	}

	@Scheduled(cron = "0 0 0 * * MON") //매주 월요일 00:00:00에 실행
    @Transactional
    public void resetSuccessRate() {
        userRepository.resetSuccessRate();
    }
}
