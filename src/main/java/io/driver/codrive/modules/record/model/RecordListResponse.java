package io.driver.codrive.modules.record.model;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordListResponse(

	@Schema(description = "풀이 목록 총 페이지 개수", examples = "5")
	int totalPage,

	@Schema(description = "문제 풀이 목록", examples = {
		"""
			[
				{
			   		"recordId": 1,
			        "title": "제목",
			        "language": "Java",
			        "tags": [
			            "정렬"
			        ],
			        "platform": "BAEKJOON",
			        "problemUrl": "PROBLEM_URL",
			        "level": 2,
			        "createdAt": "2024.02.05 12시 00분"
				}
			]
			"""
	})
	List<RecordDetailResponse> records

) {
	public static RecordListResponse of(List<Record> records) {
		return RecordListResponse.builder()
			.totalPage(0)
			.records(RecordDetailResponse.of(records))
			.build();
	}

	public static RecordListResponse of(int totalPage, Page<Record> records) {
		return RecordListResponse.builder()
			.totalPage(totalPage)
			.records(RecordDetailResponse.of(records))
			.build();
	}
}
