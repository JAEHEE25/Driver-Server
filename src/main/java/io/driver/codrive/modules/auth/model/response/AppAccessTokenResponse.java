package io.driver.codrive.modules.auth.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AppAccessTokenResponse(
	@Schema(description = "발급한 Access Token", example = "ACCESS_TOKEN")
	String accessToken
) {
	public static AppAccessTokenResponse of(String accessToken) {
		return AppAccessTokenResponse.builder()
			.accessToken(accessToken)
			.build();
	}
}
