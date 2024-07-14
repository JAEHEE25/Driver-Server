package io.driver.codrive.modules.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SampleDto {
    private String code;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;

    public static SampleDto createRequest(String clientId, String clientSecret, String code) {
        return SampleDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();
    }
}
