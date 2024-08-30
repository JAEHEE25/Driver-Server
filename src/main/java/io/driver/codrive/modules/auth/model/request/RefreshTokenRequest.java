package io.driver.codrive.modules.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenRequest(
	@Schema(description = "만료된 Access Token", example = "EXPIRED_ACCESS_TOKEN")
	String accessToken,

	@Schema(description = "Refresh Token", example = "REFRESH_TOKEN")
	String refreshToken
) {}
