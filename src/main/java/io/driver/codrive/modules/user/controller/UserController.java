package io.driver.codrive.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.user.model.request.GithubRepositoryNameRequest;
import io.driver.codrive.modules.user.model.response.*;
import io.driver.codrive.modules.user.model.request.GoalChangeRequest;
import io.driver.codrive.modules.user.model.request.NicknameRequest;
import io.driver.codrive.modules.user.model.request.ProfileChangeRequest;
import io.driver.codrive.modules.user.service.UserAchievementService;
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
	private final UserAchievementService userAchievementService;

	@Operation(
		summary = "사용자 정보 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
			@ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(value = "{\"code\": 403, \"message\": \"해당 리소스에 대한 권한이 없습니다.\"}"))),
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
			@ApiResponse(responseCode = "400", content = @Content(examples = {
				@ExampleObject(value = "{\"code\": 400, \"message\": \"잘못된 요청입니다. (error field 제공)\"}"),
			})),
			@ApiResponse(responseCode = "409", content = @Content(examples = @ExampleObject(value = "{\"code\": 409, \"message\": \"닉네임이 이미 존재합니다.\"}"))),
		}
	)
	@PostMapping("/nickname")
	public ResponseEntity<BaseResponse<Void>> checkNicknameDuplication(@Valid @RequestBody NicknameRequest request) {
		userService.checkNicknameDuplication(request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "GitHub Repository 이름 검사",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = GithubRepositoryNameRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"레포지토리가 존재하지 않습니다.\"}"))),
		}
	)
	@PostMapping("/repository")
	public ResponseEntity<BaseResponse<Void>> checkGithubRepositoryName(@RequestBody GithubRepositoryNameRequest request) {
		userService.checkGithubRepositoryName(request);
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
			@ApiResponse(responseCode = "400",
				content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 언어입니다. || 잘못된 요청입니다. (error field 제공)\"}"))),
			@ApiResponse(responseCode = "404",
				content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping("/{userId}/profile")
	public ResponseEntity<BaseResponse<ProfileChangeResponse>> changeProfile(@PathVariable(name = "userId") Long userId,
		@Valid @RequestBody ProfileChangeRequest request) {
		ProfileChangeResponse response = userService.updateCurrentUserProfile(userId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "목표 변경",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = GoalChangeRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserGoalResponse.class))),
			@ApiResponse(responseCode = "400",
				content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"잘못된 요청입니다. (error field 제공)\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping("/{userId}/goal")
	public ResponseEntity<BaseResponse<UserGoalResponse>> changeGoal(@PathVariable(name = "userId") Long userId,
		@Valid @RequestBody GoalChangeRequest request) {
		UserGoalResponse response = userService.updateCurrentUserGoal(userId, request);
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
		summary = "팔로잉 목록 조회",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserListResponse.class))),
		}
	)
	@GetMapping("/followings")
	public ResponseEntity<BaseResponse<FollowListResponse>> getFollowings() {
		FollowListResponse response = userService.getFollowings();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "팔로워 목록 조회",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserListResponse.class))),
		}
	)
	@GetMapping("/followers")
	public ResponseEntity<BaseResponse<FollowListResponse>> getFollowers() {
		FollowListResponse response = userService.getFollowers();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "사용자 성과 조회",
		description = "현재 로그인한 사용자가 설정한 목표, 오늘 푼 문제 개수, 성과율, 이번 주 기준 지난 주와의 문제 풀이 개수 차이를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserAchievementResponse.class))),
		}
	)
	@GetMapping("/achieve")
	public ResponseEntity<BaseResponse<UserAchievementResponse>> getAchievement() {
		UserAchievementResponse response = userAchievementService.getAchievement();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "프로필 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/profile")
	public ResponseEntity<BaseResponse<UserProfileResponse>> getProfile(@PathVariable(name = "userId") Long userId) {
		UserProfileResponse response = userService.getProfile(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

}
