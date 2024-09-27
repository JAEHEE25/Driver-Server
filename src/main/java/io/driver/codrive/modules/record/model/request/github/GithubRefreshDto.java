package io.driver.codrive.modules.record.model.request.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubRefreshDto {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

	@JsonProperty("grant_type")
	private String grantType;

	@JsonProperty("refresh_token")
	private String refreshToken;

	public static GithubRefreshDto of(String clientId, String clientSecret, String refreshToken) {
		return GithubRefreshDto.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.grantType("refresh_token")
				.refreshToken(refreshToken)
				.build();
	}
}