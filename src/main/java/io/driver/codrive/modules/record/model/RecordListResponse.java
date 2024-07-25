package io.driver.codrive.modules.record.model;

import java.util.List;

import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import lombok.Builder;

@Builder
public record RecordListResponse(
	List<RecordListDetailResponse> records
) {
	public static RecordListResponse of(List<Record> records) {
		return RecordListResponse.builder()
			.records(records.stream().map(RecordListDetailResponse::of).toList())
			.build();
	}

	@Builder
	record RecordListDetailResponse(
		Long recordId,
		String title,
		String language,
		List<String> tags,
		Platform platform,
		String problemUrl,
		int level

	) {
		public static RecordListDetailResponse of(Record record) {
			return RecordListDetailResponse.builder()
				.recordId(record.getRecordId())
				.title(record.getTitle())
				.language(record.getUser().getLanguage().getName())
				.tags(record.getTags())
				.platform(record.getPlatform())
				.problemUrl(record.getProblemUrl())
				.level(record.getLevel())
				.build();
		}
	}
}
