package io.driver.codrive.modules.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.AlreadyExistsApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.domain.UserRepository;
import io.driver.codrive.modules.user.model.*;
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
		user.changeNickname(request.nickname());
		user.changeLanguage(languageService.getLanguageByName(request.language()));
		user.changeComment(request.comment());
		user.changeGithubUrl(request.githubUrl());
		return ProfileChangeResponse.of(user);
	}

	@Transactional
	public void updateCurrentUserWithdraw(Long userId) {
		User user = getUserById(userId);
		userRepository.delete(user);
	}

	@Transactional
	public JoinedRoomListResponse getJoinedRoomList(Long userId) {
		User user = getUserById(userId);
		return JoinedRoomListResponse.of(user.getJoinedRooms());
	}

	@Transactional
	public CreatedRoomListResponse getCreatedRoomList(Long userId) {
		User user = getUserById(userId);
		return CreatedRoomListResponse.of(user.getCreatedRooms());
	}

	public void changeUserRole(User user, Role role) {
		user.changeRole(role);
	}

}
