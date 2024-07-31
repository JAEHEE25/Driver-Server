package io.driver.codrive.modules.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.auth.model.LoginRequest;
import io.driver.codrive.modules.auth.model.LoginResponse;
import io.driver.codrive.modules.auth.service.AuthService;
import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
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

	@Value("${github.client_id}")
	private String clientId;

	@Value("${github.client_secret}")
	private String clientSecret;

	@Operation(
		summary = "소셜 로그인",
		description = "GitHub 소셜 로그인 API입니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = LoginRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = {@ExampleObject(value = "{\"code\": 400, \"message\": \"잘못된 요청입니다. (error field 제공)\"}"),
			})),
			@ApiResponse(responseCode = "401", content = @Content(examples = @ExampleObject(value = "{\"code\": 401, \"message\": \"유효하지 않은 토큰입니다.\"}"))),
		}
	)
	@PostMapping("/login")
	public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
		LoginResponse response = authService.socialLogin(loginRequest);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	//로컬 테스트용
	@PostMapping("/create")
	public ResponseEntity<BaseResponse<Void>> createUser() {
		authService.createUser();
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
