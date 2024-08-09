package io.driver.codrive.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.auth.model.LoginResponse;
import io.driver.codrive.modules.auth.service.AuthService;
import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
		parameters = {
			@Parameter(name = "code", in = ParameterIn.QUERY, required = true, description = "GitHub에서 발급 받은 코드")
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "401", content = @Content(examples = @ExampleObject(value = "{\"code\": 401, \"message\": \"유효하지 않은 코드입니다. || 유효하지 않은 토큰입니다.\"}"))),
		}
	)
	@PostMapping("/login")
	public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestParam(name = "code") String code) {
		LoginResponse response = authService.socialLogin(code);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	//로컬 테스트용
	@PostMapping("/create")
	public ResponseEntity<BaseResponse<Void>> createUser() {
		authService.createUser();
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
