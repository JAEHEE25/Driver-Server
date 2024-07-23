package io.driver.codrive.modules.codeblock.model;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import lombok.Builder;

@Builder
public record CodeblockDetailResponse(
	String code,
	String memo
) {
	public static CodeblockDetailResponse of(Codeblock codeblock) {
		return CodeblockDetailResponse.builder()
			.code(codeblock.getCode())
			.memo(codeblock.getMemo())
			.build();
	}
}
