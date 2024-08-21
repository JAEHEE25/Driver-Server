package io.driver.codrive.modules.record.model.response;

import java.util.List;

import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordRecentListResponse(
	@Schema(description = "문제 풀이 목록")
	List<RecordItemResponse> records
) {
	public static RecordRecentListResponse of(List<Record> records) {
		return RecordRecentListResponse.builder()
			.records(RecordItemResponse.of(records))
			.build();
	}
}
