package io.driver.codrive.modules.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record GithubLoginRequest(
	@Schema(description = "Github 로그인 코드", example = "12345678")
	String code
) {
}
