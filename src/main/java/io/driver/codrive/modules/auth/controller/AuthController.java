package io.driver.codrive.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.auth.model.request.GithubLoginRequest;
import io.driver.codrive.modules.auth.model.request.RefreshTokenRequest;
import io.driver.codrive.modules.auth.model.response.AccessTokenResponse;
import io.driver.codrive.modules.auth.model.response.LoginResponse;
import io.driver.codrive.modules.auth.service.AuthService;
import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@Operation(
		summary = "소셜 로그인",
		description = "GitHub 소셜 로그인 API입니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = GithubLoginRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "401", content = @Content(examples = @ExampleObject(value = "{\"code\": 401, \"message\": \"유효하지 않은 코드입니다. || 유효하지 않은 토큰입니다.\"}"))),
		}
	)
	@PostMapping("/login")
	public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody GithubLoginRequest request) {
		LoginResponse response = authService.socialLogin(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "토큰 갱신",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = RefreshTokenRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
			@ApiResponse(responseCode = "401", content = @Content(examples = @ExampleObject(value = "{\"code\": 401, \"message\": \"Refresh Token이 유효하지 않습니다. Refresh Token이 만료되었습니다.\"}"))),
		}
	)
	@PostMapping("/refresh")
	public ResponseEntity<BaseResponse<AccessTokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
		AccessTokenResponse response = authService.refresh(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "사용자 추가 (로컬 테스트용)",
		description = "가상의 사용자를 추가할 수 있는 API입니다.",
		responses = {
			@ApiResponse(responseCode = "200"),
		}
	)
	@PostMapping("/addUser")
	public ResponseEntity<BaseResponse<Void>> addUser() {
		authService.addUser();
		return ResponseEntity.ok(BaseResponse.of(null));
	}
}
