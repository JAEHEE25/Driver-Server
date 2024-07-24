package io.driver.codrive.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.user.model.*;
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

	@PostMapping("/nickname")
	public ResponseEntity<BaseResponse<Void>> checkNicknameDuplication(@Valid @RequestBody NicknameRequest request) {
		userService.checkNicknameDuplication(request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@PatchMapping("/{userId}/profile")
	public ResponseEntity<BaseResponse<ProfileChangeResponse>> changeProfile(@PathVariable(name = "userId") Long userId,
		@Valid @RequestBody ProfileChangeRequest request) {
		ProfileChangeResponse response = userService.updateCurrentUserProfile(userId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@DeleteMapping("/{userId}/withdraw")
	public ResponseEntity<BaseResponse<Void>> withdraw(@PathVariable(name = "userId") Long userId) {
		userService.updateCurrentUserWithdraw(userId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@GetMapping("/{userId}/rooms")
	public ResponseEntity<BaseResponse<JoinedRoomListResponse>> getJoinedRoomList(@PathVariable(name = "userId") Long userId) {
		JoinedRoomListResponse response = userService.getJoinedRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@GetMapping("/{userId}/rooms/owner")
	public ResponseEntity<BaseResponse<CreatedRoomListResponse>> getCreatedRoomList(@PathVariable(name = "userId") Long userId) {
		CreatedRoomListResponse response = userService.getCreatedRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

}
