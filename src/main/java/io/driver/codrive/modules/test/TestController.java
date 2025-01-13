package io.driver.codrive.modules.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.auth.JwtProvider;
import io.driver.codrive.global.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test API", description = "테스트 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/test")
@RequiredArgsConstructor
public class TestController {
	private final JwtProvider jwtProvider;

	@Operation(
		summary = "테스트 토큰 발급",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\", \"data\": \"ACCESS_TOKEN\"}"))),
		}
	)
	@GetMapping("/token/{userId}")
	public ResponseEntity<BaseResponse<String>> createTestToken(@PathVariable(name = "userId") Long userId) {
		String token = jwtProvider.generateAccessToken(userId);
		return ResponseEntity.ok(BaseResponse.of(token));
	}
}
