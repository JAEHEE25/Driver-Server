package io.driver.codrive.modules.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ProfileChangeRequest(
	@Schema(description = "닉네임", example = "닉네임", minLength = 1, maxLength = 10)
	@Size(min = 1, max = 10, message = "닉네임은 {min}자 이상, {max}자 이하로 입력해주세요.")
	String nickname,

	@Schema(description = "주 언어", example = "Java")
	String language,

	@Schema(description = "한 줄 소개", example = "한 줄 소개")
	@Size(max = 30, message = "한 줄 소개는 {max}자 이하로 입력해주세요.")
	String comment,

	@Schema(description = "GitHub URL", example = "GITHUB_URL")
	String githubUrl
) {
}
