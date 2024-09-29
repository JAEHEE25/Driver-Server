package io.driver.codrive.modules.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GithubRepositoryNameRequest(
	@Schema(description = "입력한 Github Repository 이름", example = "Algorithm")
	@NotBlank(message = "레포지토리 이름을 입력해주세요.")
	String githubRepositoryName
) {
}