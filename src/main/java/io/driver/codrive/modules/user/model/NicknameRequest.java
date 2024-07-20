package io.driver.codrive.modules.user.model;

import jakarta.validation.constraints.Size;

public record NicknameRequest(
	@Size(min = 1, max = 10, message = "닉네임은 {min}자 이상, {max}자 이하로 입력해주세요.")
	String nickname
) {
}
