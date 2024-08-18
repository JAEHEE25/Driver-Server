package io.driver.codrive.modules.mappings.roomUserMapping.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageMemberCountDto {
	@Schema(description = "언어", example = "Java")
	private String language;

	@Schema(description = "인원 수", example = "5")
	private Long count;
}
