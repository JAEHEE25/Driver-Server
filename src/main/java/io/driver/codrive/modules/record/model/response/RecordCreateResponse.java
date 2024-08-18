package io.driver.codrive.modules.record.model.response;

import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordCreateResponse(
	@Schema(description = "등록된 문제 풀이 ID", example = "1")
	Long recordId
) {
	public static RecordCreateResponse of(Record record) {
		return RecordCreateResponse.builder()
			.recordId(record.getRecordId())
			.build();
	}
}
