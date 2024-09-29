package io.driver.codrive.modules.record.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubAccessTokenDto {
    @JsonProperty("access_token")
    private String accessToken;

    public static GithubAccessTokenDto of(String accessToken) {
        return GithubAccessTokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
