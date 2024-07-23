package io.driver.codrive.modules.record.model;

import io.driver.codrive.modules.record.domain.Record;
import lombok.Builder;

@Builder
public record RecordCreateResponse(
	Long recordId
) {
	public static RecordCreateResponse of(Record record) {
		return RecordCreateResponse.builder()
			.recordId(record.getRecordId())
			.build();
	}
}
