package io.driver.codrive.modules.follow.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.follow.domain.Follow;
import io.driver.codrive.modules.follow.domain.FollowRepository;
import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final UserService userService;
	private final FollowRepository followRepository;

	@Transactional
	public void follow(String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());

		if (target.equals(currentUser)) {
			throw new IllegalArgumentApplicationException("자기 자신을 팔로우할 수 없습니다.");
		}

		if (getFollowByUsers(target, currentUser) != null) {
			throw new AlreadyExistsApplicationException("팔로우 데이터");
		}

		Follow follow = Follow.toFollow(target, currentUser);
		currentUser.addFollowing(follow);
		target.addFollower(follow);
		followRepository.save(follow);
	}

	@Transactional
	public void cancelFollow(String nickname) {
		User target = userService.getUserByNickname(nickname);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());

		Follow follow = getFollowByUsers(target, currentUser);
		if (follow == null) {
			throw new NotFoundApplcationException("팔로우 데이터");
		}
		followRepository.delete(follow);
		target.deleteFollower(follow);
		currentUser.deleteFollowing(follow);
	}

	private Follow getFollowByUsers(User following, User follower) {
		return followRepository.findByFollowingAndFollower(following, follower).orElse(null);
	}

}
