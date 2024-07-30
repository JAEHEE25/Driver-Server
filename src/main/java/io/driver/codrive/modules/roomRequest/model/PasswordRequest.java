package io.driver.codrive.modules.roomRequest.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordRequest(
	@Schema(description = "비밀번호", example = "비밀번호")
	String password
) {
}
