package io.driver.codrive.global.token;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "appToken", timeToLive = 1209600000)
public class AppToken {
    @Id
    private String accessToken;

    private String refreshToken;

    private Long userId;
}