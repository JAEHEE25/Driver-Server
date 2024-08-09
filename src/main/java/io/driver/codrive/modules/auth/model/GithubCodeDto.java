package io.driver.codrive.modules.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubCodeDto {
    private String code;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    public static GithubCodeDto createRequest(String clientId, String clientSecret, String code) {
        return GithubCodeDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();
    }
}