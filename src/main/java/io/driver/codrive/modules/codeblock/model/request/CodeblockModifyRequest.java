package io.driver.codrive.modules.codeblock.model.request;

import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CodeblockModifyRequest(
	@Schema(description = "코드", example = "code")
	@NotBlank(message = "코드를 입력해주세요.")
	@Size(max = 15000, message = "코드는 {max}자 이하로 입력해주세요.")
	String code,

	@Schema(description = "메모", example = "memo")
	@Size(max = 15000, message = "메모는 {max}자 이하로 입력해주세요.")
	String memo
) {
	public static List<Codeblock> of(List<CodeblockModifyRequest> requests, Record record) {
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
