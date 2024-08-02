package io.driver.codrive.modules.codeblock.model;

import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CodeblockCreateRequest(
	@Schema(description = "코드", example = "code")
	String code,

	@Schema(description = "메모", example = "memo")
	String memo
) {
	public static List<Codeblock> of(List<CodeblockCreateRequest> requests, Record record) {
		return requests.stream().map(request -> request.toEntity(record)).toList();
	}

	public Codeblock toEntity(Record record) {
		return Codeblock.builder()
			.code(code)
			.memo(memo)
			.record(record)
			.build();
	}
}
