package io.driver.codrive.modules.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.auth.model.LoginRequest;
import io.driver.codrive.modules.auth.model.LoginResponse;
import io.driver.codrive.modules.auth.service.AuthService;
import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@Value("${github.client_id}")
	private String clientId;

	@Value("${github.client_secret}")
	private String clientSecret;

	//로컬 테스트용
	@PostMapping("/login/token")
	public void getSampleCode(@RequestParam String code) {
		authService.getAccessToken(clientId, clientSecret, code);
	}

	@PostMapping("/login")
	public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
		LoginResponse response = authService.socialLogin(loginRequest);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
