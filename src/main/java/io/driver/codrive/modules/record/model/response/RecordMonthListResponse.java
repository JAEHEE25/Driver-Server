package io.driver.codrive.modules.record.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordMonthListResponse(

	@Schema(description = "풀이 목록 총 페이지 개수", examples = "5")
	int totalPage,

	@Schema(description = "문제 풀이 목록", examples = {
		"""
			[
				{
			   		"recordId": 1,
			        "title": "제목",
			        "level": 2,
			        "tags": [
			            "정렬"
			        ],
			        "platform": "백준",
			        "problemUrl": "PROBLEM_URL",
			        "createdAt": "2월 5일"
				}
			]
			"""
	})
	List<MonthRecordResponse> records

) {
	public static RecordMonthListResponse of(int totalPage, Page<Record> records) {
		return RecordMonthListResponse.builder()
			.totalPage(totalPage)
			.records(MonthRecordResponse.of(records))
			.build();
	}

	@Builder
	record MonthRecordResponse(
		@Schema(description = "문제 풀이 ID", example = "1")
		Long recordId,

		@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
		String title,

		@Schema(description = "난이도", example = "1")
		int level,

		@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]")
		List<String> tags,

		@Schema(description = "문제 플랫폼", example = "백준")
		String platform,

		@Schema(description = "문제 URL", example = "https://codrive.co.kr")
		String problemUrl,

		@Schema(description = "작성 일자", example = "2월 5일")
		String createdAt

	) {
		public static List<MonthRecordResponse> of(Page<Record> records) {
			return records.stream().map(MonthRecordResponse::of).toList();
		}

		public static MonthRecordResponse of(Record record) {
			return MonthRecordResponse.builder()
				.recordId(record.getRecordId())
				.title(record.getTitle())
				.level(record.getLevel())
				.tags(record.getCategories())
				.platform(record.getPlatform().getName())
				.problemUrl(record.getProblemUrl())
				.createdAt(DateUtils.formatCreatedAtByMD(record.getCreatedAt()))
				.build();
		}
	}
}

