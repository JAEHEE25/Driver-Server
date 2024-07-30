package io.driver.codrive.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.user.model.*;
import io.driver.codrive.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@Operation(
		summary = "사용자 정보 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}")
	public ResponseEntity<BaseResponse<UserDetailResponse>> getUserInfo(@PathVariable(name = "userId") Long userId) {
		UserDetailResponse response = userService.getUserInfo(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "닉네임 중복 검사",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = NicknameRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "409", content = @Content(examples = @ExampleObject(value = "{\"code\": 409, \"message\": \"닉네임이 이미 존재합니다.\"}"))),
		}
	)
	@PostMapping("/nickname")
	public ResponseEntity<BaseResponse<Void>> checkNicknameDuplication(@Valid @RequestBody NicknameRequest request) {
		userService.checkNicknameDuplication(request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "프로필 변경",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = ProfileChangeRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProfileChangeResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 언어입니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping("/{userId}/profile")
	public ResponseEntity<BaseResponse<ProfileChangeResponse>> changeProfile(@PathVariable(name = "userId") Long userId,
		@Valid @RequestBody ProfileChangeRequest request) {
		ProfileChangeResponse response = userService.updateCurrentUserProfile(userId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "사용자 탈퇴",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@DeleteMapping("/{userId}/withdraw")
	public ResponseEntity<BaseResponse<Void>> withdraw(@PathVariable(name = "userId") Long userId) {
		userService.updateCurrentUserWithdraw(userId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "사용자가 참여한 그룹 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JoinedRoomListResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/rooms")
	public ResponseEntity<BaseResponse<JoinedRoomListResponse>> getJoinedRoomList(@PathVariable(name = "userId") Long userId) {
		JoinedRoomListResponse response = userService.getJoinedRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "사용자가 생성한 그룹 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CreatedRoomListResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/rooms/owner")
	public ResponseEntity<BaseResponse<CreatedRoomListResponse>> getCreatedRoomList(@PathVariable(name = "userId") Long userId) {
		CreatedRoomListResponse response = userService.getCreatedRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

}
