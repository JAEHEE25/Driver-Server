package io.driver.codrive.modules.record.model;

import java.util.List;

import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordListResponse(
	@Schema(description = "문제 풀이 목록", implementation = RecordDetailResponse.class)
	List<RecordDetailResponse> records
) {
	public static RecordListResponse of(List<Record> records) {
		return RecordListResponse.builder()
			.records(RecordDetailResponse.of(records))
			.build();
	}
}
