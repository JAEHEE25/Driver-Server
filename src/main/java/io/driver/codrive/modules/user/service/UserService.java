package io.driver.codrive.modules.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.user.domain.Language;
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

	public UserInfoResponse getMyProfile() {
		User user = getCurrentUser();
		return UserInfoResponse.of(user);
	}

	@Transactional
	public ProfileChangeResponse updateCurrentUserProfile(ProfileChangeRequest request) {
		User user = getCurrentUser();
		user.changeNickname(request.nickname());
		user.changeLanguage(Language.of(request.language()));
		user.changeGithubUrl(request.githubUrl());
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	public void updateCurrentUserWithdraw() {
		User user = getCurrentUser();
		userRepository.delete(user);
	}

	public void checkNicknameDuplication(NicknameRequest request) {
		if (userRepository.existsByNickname(request.nickname())) {
			throw new AlreadyExistsApplicationException("닉네임");
		}
	}

	private User getCurrentUser() {
		return userRepository.findById(AuthUtils.getCurrentUserId())
			.orElseThrow(() -> new NotFoundApplcationException("사용자"));
	}
}
