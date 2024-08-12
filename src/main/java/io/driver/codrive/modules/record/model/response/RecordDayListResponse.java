package io.driver.codrive.modules.record.model.response;

import java.util.List;

import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordDayListResponse(

	@Schema(description = "날짜별 문제 풀이 목록", examples = {
		"""
			[
				{
			   		"recordId": 1,
			        "language": "Java",
			        "title": "제목",
			        "level": 2,
			        "tags": [
			            "정렬"
			        ],
			        "problemUrl": "PROBLEM_URL"
				}
			]
			"""
	})
	List<DayRecordResponse> records
) {
	public static RecordDayListResponse of(List<Record> records) {
		return RecordDayListResponse.builder()
			.records(DayRecordResponse.of(records))
			.build();
	}

	@Builder
	record DayRecordResponse(
		@Schema(description = "문제 풀이 ID", example = "1")
		Long recordId,

		@Schema(description = "문제 풀이 언어", example = "JavaScript")
		String language,

		@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
		String title,

		@Schema(description = "난이도", example = "1")
		int level,

		@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]")
		List<String> tags,

		@Schema(description = "문제 URL", example = "PROBLEM_URL")
		String problemUrl
	) {
		public static List<DayRecordResponse> of(List<Record> records) {
			return records.stream().map(DayRecordResponse::of).toList();
		}

		public static DayRecordResponse of(Record record) {
			return DayRecordResponse.builder()
				.recordId(record.getRecordId())
				.language(record.getUser().getLanguage().getName())
				.title(record.getTitle())
				.level(record.getLevel())
				.tags(record.getCategories())
				.problemUrl(record.getProblemUrl())
				.build();
		}
	}
}
