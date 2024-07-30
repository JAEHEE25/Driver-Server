package io.driver.codrive.modules.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
	@Schema(description = "GitHub에서 발급 받은 코드", example = "ACCESS_TOKEN")
	String accessToken
) {}
