package io.driver.codrive.modules.codeblock.model.request;

import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CodeblockCreateRequest(
	@Schema(description = "코드", example = "code")
	@Size(max = 15000, message = "코드는 {max}자 이하로 입력해주세요.")
	String code,

	@Schema(description = "메모", example = "memo")
	@Size(max = 15000, message = "메모는 {max}자 이하로 입력해주세요.")
	String memo
) {
	public static List<Codeblock> of(List<CodeblockCreateRequest> requests, Record record) {
		return requests.stream().map(request -> request.toCodeblock(record)).toList();
	}

	public Codeblock toCodeblock(Record record) {
		return Codeblock.builder()
			.code(code)
			.memo(memo)
			.record(record)
			.build();
	}
}
