package io.driver.codrive.global.token;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "githubToken", timeToLive = 1209600000)
public class GithubToken {
    @Id
    private Long userId;

    private String accessToken;

    private String refreshToken;
}