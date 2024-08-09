package io.driver.codrive.modules.follow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.follow.service.FollowService;
import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

}
