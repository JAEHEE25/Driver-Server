package io.driver.codrive.modules.codeblock.model;

import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CodeblockTempRequest(
	@Schema(description = "코드", example = "code")
	String code,

	@Schema(description = "메모", example = "memo")
	String memo
) {
	public static List<Codeblock> of(List<CodeblockTempRequest> requests, Record record) {
		return requests.stream()
			.map(request -> request.of(record))
			.toList();
	}

	public Codeblock of(Record record) {
		return Codeblock.builder()
			.code(code)
			.memo(memo)
			.record(record)
			.build();
	}
}
