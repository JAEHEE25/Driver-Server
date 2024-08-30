package io.driver.codrive.modules.auth.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AccessTokenResponse(
	@Schema(description = "발급한 Access Token", example = "ACCESS_TOKEN")
	String accessToken
) {
	public static AccessTokenResponse of(String accessToken) {
		return AccessTokenResponse.builder()
			.accessToken(accessToken)
			.build();
	}
}
