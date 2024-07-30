package io.driver.codrive.modules.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record NicknameRequest(
	@Schema(description = "입력한 닉네임", example = "닉네임", minLength = 1, maxLength = 10)
	@Size(min = 1, max = 10, message = "닉네임은 {min}자 이상, {max}자 이하로 입력해주세요.")
	String nickname
) {
}
