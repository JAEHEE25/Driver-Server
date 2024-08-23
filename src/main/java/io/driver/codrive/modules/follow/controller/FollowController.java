package io.driver.codrive.modules.follow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.follow.model.response.FollowingSummaryListResponse;
import io.driver.codrive.modules.follow.model.response.FollowingWeeklyCountResponse;
import io.driver.codrive.modules.follow.model.response.TodaySolvedFollowingResponse;
import io.driver.codrive.modules.follow.model.response.WeeklyFollowingResponse;
import io.driver.codrive.modules.follow.service.FollowService;
import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.user.model.response.UserListResponse;
import io.driver.codrive.modules.user.model.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Follow API", description = "팔로우 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/follow")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;

	@Operation(
		summary = "팔로우",
		parameters = {
			@Parameter(name = "nickname", in = ParameterIn.PATH, required = true, description = "팔로우할 사용자 닉네임"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"자기 자신을 팔로우할 수 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
			@ApiResponse(responseCode = "409", content = @Content(examples = @ExampleObject(value = "{\"code\": 409, \"message\": \"팔로우 데이터가 이미 존재합니다.\"}"))),
		}
	)
	@PostMapping("/{nickname}")
	public ResponseEntity<BaseResponse<Void>> follow(@PathVariable(name = "nickname") String nickname) {
		followService.follow(nickname);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "팔로우 취소",
		parameters = {
			@Parameter(name = "nickname", in = ParameterIn.PATH, required = true, description = "팔로우를 취소할 사용자 닉네임"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
			@ApiResponse(responseCode = "409", content = @Content(examples = @ExampleObject(value = "{\"code\": 409, \"message\": \"팔로우 데이터를 찾을 수 없습니다.\"}"))),
		}
	)
	@DeleteMapping("/{nickname}")
	public ResponseEntity<BaseResponse<Void>> cancelFollow(@PathVariable(name = "nickname") String nickname) {
		followService.cancelFollow(nickname);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "추천 사용자 목록 조회",
		description = "자기자신 및 이미 팔로우 중인 사용자를 제외하고 랜덤으로 6명을 추천합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserListResponse.class))),
		}
	)
	@GetMapping("/recommend")
	public ResponseEntity<BaseResponse<UserListResponse>> getRandomUsers() {
		UserListResponse response = followService.getRandomUsers();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "팔로잉 주간 문제 풀이 개수 조회",
		description = "팔로우한 사용자들의 주간 문제 풀이 개수를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FollowingWeeklyCountResponse.class))),
		}
	)
	@GetMapping("/followings/weekly-count")
	public ResponseEntity<BaseResponse<FollowingWeeklyCountResponse>> getFollowingsWeeklyCount() {
		FollowingWeeklyCountResponse response = followService.getFollowingsWeeklyCount();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "오늘 문제를 푼 팔로잉 목록 조회",
		description = "팔로우한 사용자들 중 오늘 문제를 푼 사용자 목록을 조회합니다.",
		parameters = {
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호")
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TodaySolvedFollowingResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/followings/today-solved")
	public ResponseEntity<BaseResponse<TodaySolvedFollowingResponse>> getTodaySolvedFollowings(@RequestParam(name = "page", defaultValue = "0") Integer page) {
		TodaySolvedFollowingResponse response = followService.getTodaySolvedFollowings(page);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "주간 팔로잉 현황 조회",
		description = "팔로우하는 사용자 3명의 현황을 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WeeklyFollowingResponse.class))),
		}
	)
	@GetMapping("/followings/weekly")
	public ResponseEntity<BaseResponse<WeeklyFollowingResponse>> getWeeklyFollowings() {
		WeeklyFollowingResponse response = followService.getWeeklyFollowings();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "팔로잉 현황 목록 조회",
		description = "팔로우한 사용자들의 요약 정보를 조회합니다.",
		parameters = {
			@Parameter(name = "sortType", in = ParameterIn.PATH, description = "페이지 정렬 기준 (NEW, DICT)"),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "groupId", in = ParameterIn.QUERY, description = "그룹 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserSummaryResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/followings/summary/{sortType}")
	public ResponseEntity<BaseResponse<FollowingSummaryListResponse>> getFollowingsSummary(
		@PathVariable(name = "sortType") SortType sortType,
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "roomId", required = false) Long roomId) {
		FollowingSummaryListResponse response = followService.getFollowingsSummary(sortType, page, roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

}
