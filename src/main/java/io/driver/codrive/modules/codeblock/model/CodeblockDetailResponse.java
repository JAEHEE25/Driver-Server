package io.driver.codrive.modules.codeblock.model;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CodeblockDetailResponse(
	@Schema(description = "문제 풀이 코드", example = "CODE")
	String code,

	@Schema(description = "문제 풀이 메모", example = "MEMO")
	String memo
) {
	public static CodeblockDetailResponse of(Codeblock codeblock) {
		return CodeblockDetailResponse.builder()
			.code(codeblock.getCode())
			.memo(codeblock.getMemo())
			.build();
	}
}
