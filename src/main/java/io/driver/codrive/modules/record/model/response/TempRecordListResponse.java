package io.driver.codrive.modules.record.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TempRecordListResponse(
	@Schema(description = "임시 저장 글 목록 총 페이지 개수", examples = "3")
	int totalPage,

	@Schema(description = "임시 저장 글 목록", examples = {
		"""
			[
				{
			   		"recordId": 1,
			        "title": "제목",
			        "level": 2,
			        "createdAt": "2024.02.05 12시 00분"
				}
			]
		"""
	}, implementation = TempRecordResponse.class)
	List<TempRecordResponse> records
) {
	public static TempRecordListResponse of(int totalPage, Page<Record> records) {
		return TempRecordListResponse.builder()
			.totalPage(totalPage)
			.records(TempRecordResponse.of(records))
			.build();
	}

	@Builder
	record TempRecordResponse(
		@Schema(description = "문제 풀이 ID", example = "1")
		Long recordId,

		@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
		String title,

		@Schema(description = "난이도", example = "1")
		int level,

		@Schema(description = "작성 일자", example = "2024.02.05 12시 00분")
		String createdAt
	) {
		public static List<TempRecordResponse> of(Page<Record> records) {
			return records.stream().map(TempRecordResponse::of).toList();
		}

		public static TempRecordResponse of(Record record) {
			return TempRecordResponse.builder()
				.recordId(record.getRecordId())
				.title(record.getTitle())
				.level(record.getLevel())
				.createdAt(DateUtils.formatCreatedAtByYMDHM(record.getCreatedAt()))
				.build();
		}
	}
}
