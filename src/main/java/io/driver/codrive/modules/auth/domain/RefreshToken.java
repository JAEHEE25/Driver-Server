package io.driver.codrive.modules.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "accessToken", timeToLive = 1209600000)
public class RefreshToken {
    @Id
    private String accessToken;

    private String refreshToken;
    private Long userId;
}