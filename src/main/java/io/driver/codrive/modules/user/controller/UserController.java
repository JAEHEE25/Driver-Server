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
@RequestMapping(APIConstants.API_PREFIX + "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<BaseResponse<UserInfoResponse>> getUserInfo(@PathVariable(name = "userId") Long userId) {
		UserInfoResponse response = userService.getUserInfo(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@PatchMapping("/profile")
	public ResponseEntity<BaseResponse<ProfileChangeResponse>> changeProfile(
		@Valid @RequestBody ProfileChangeRequest request) {
		ProfileChangeResponse response = userService.updateCurrentUserProfile(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@DeleteMapping("/withdraw")
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
