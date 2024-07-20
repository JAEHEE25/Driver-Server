package io.driver.codrive.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.user.model.NicknameRequest;
import io.driver.codrive.modules.user.model.ProfileChangeRequest;
import io.driver.codrive.modules.user.model.ProfileChangeResponse;
import io.driver.codrive.modules.user.model.UserInfoResponse;
import io.driver.codrive.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<BaseResponse<UserInfoResponse>> getMyProfile() {
		UserInfoResponse response = userService.getMyProfile();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@PatchMapping("/profile")
	public ResponseEntity<BaseResponse<ProfileChangeResponse>> changeProfile(
		@Valid @RequestBody ProfileChangeRequest request) {
		ProfileChangeResponse response = userService.updateCurrentUserProfile(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@PatchMapping("/withdraw")
	public ResponseEntity<BaseResponse<Void>> withdraw() {
		userService.updateCurrentUserWithdraw();
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@PostMapping("/nickname")
	public ResponseEntity<BaseResponse<Void>> checkNicknameDuplication(@Valid @RequestBody NicknameRequest request) {
		userService.checkNicknameDuplication(request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
