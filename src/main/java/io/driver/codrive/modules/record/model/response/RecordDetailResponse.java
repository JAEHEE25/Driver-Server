package io.driver.codrive.modules.record.model.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.codeblock.model.response.CodeblockDetailResponse;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordDetailResponse(
	@Schema(description = "문제 풀이 ID", example = "1")
	Long recordId,

	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	String title,

	@Schema(description = "난이도", example = "1")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]")
	List<String> tags,

	@Schema(description = "문제 플랫폼", example = "백준")
	Platform platform,

	@Schema(description = "문제 URL", example = "PROBLEM_URL")
	String problemUrl,

	@Schema(description = "작성한 코드 블록", implementation = CodeblockDetailResponse.class)
	List<CodeblockDetailResponse> codeblocks,

	@Schema(description = "작성 일자", example = "2024.02.05 12시 00분")
	String createdAt
) {
	public static List<RecordDetailResponse> of(List<Record> records) {
		return records.stream().map(RecordDetailResponse::of).toList();
	}

	public static List<RecordDetailResponse> of(Page<Record> records) {
		return records.stream().map(RecordDetailResponse::of).toList();
	}

	public static RecordDetailResponse of(Record record) {
		return RecordDetailResponse.builder()
			.recordId(record.getRecordId())
			.title(record.getTitle())
			.level(record.getLevel())
			.tags(record.getCategories())
			.platform(record.getPlatform())
			.problemUrl(record.getProblemUrl())
			.codeblocks(record.getCodeblocks().stream().map(CodeblockDetailResponse::of).toList())
			.createdAt(DateUtils.formatCreatedAtByYMDHM(record.getCreatedAt()))
			.build();
	}
}
