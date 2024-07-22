package io.driver.codrive.modules.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.user.domain.Language;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.NicknameRequest;
import io.driver.codrive.modules.user.model.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.ProfileChangeResponse;
import io.driver.codrive.modules.user.model.UserInfoResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public UserInfoResponse getUserInfo(Long userId) {
		User user = getUserById(userId);
		return UserInfoResponse.of(user);
	}

	@Transactional
	public ProfileChangeResponse updateCurrentUserProfile(ProfileChangeRequest request) {
		User user = getUserById(AuthUtils.getCurrentUserId());
		user.changeNickname(request.nickname());
		user.changeLanguage(Language.of(request.language()));
		user.changeGithubUrl(request.githubUrl());
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	public void updateCurrentUserWithdraw() {
		User user = getUserById(AuthUtils.getCurrentUserId());
		userRepository.delete(user);
	}

	public void checkNicknameDuplication(NicknameRequest request) {
		if (userRepository.existsByNickname(request.nickname())) {
			throw new AlreadyExistsApplicationException("닉네임");
		}
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundApplcationException("사용자"));
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> new NotFoundApplcationException("사용자"));
	}

	public void changeUserRole(User user, Role role) {
		user.changeRole(role);
	}
}
