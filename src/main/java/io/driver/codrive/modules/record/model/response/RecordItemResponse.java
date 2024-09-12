package io.driver.codrive.modules.record.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordItemResponse(
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

	@Schema(description = "작성 일자", example = "2.5")
	String createdAt

) {
	public static List<RecordItemResponse> of(Page<Record> records) {
		return records.stream().map(RecordItemResponse::of).toList();
	}

	public static List<RecordItemResponse> of(List<Record> records) {
		return records.stream().map(RecordItemResponse::of).toList();
	}

	public static RecordItemResponse of(Record record) {
		return RecordItemResponse.builder()
			.recordId(record.getRecordId())
			.title(record.getTitle())
			.level(record.getLevel())
			.tags(record.getCategories())
			.platform(record.getPlatformName())
			.problemUrl(record.getProblemUrl())
			.createdAt(DateUtils.formatCreatedAtByMD(record.getCreatedAt()))
			.build();
	}
}
