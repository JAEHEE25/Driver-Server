package io.driver.codrive.modules.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.jwt.JwtProvider;
import io.driver.codrive.modules.global.model.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/test")
@RequiredArgsConstructor
public class TestController {
	private final JwtProvider jwtProvider;

	@GetMapping("/token/{userId}")
	public ResponseEntity<BaseResponse<String>> createTestToken(@PathVariable(name = "userId") Long userId) {
		String token = jwtProvider.generateAccessToken(userId);
		return ResponseEntity.ok(BaseResponse.of(token));
	}
}
