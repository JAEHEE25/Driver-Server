package io.driver.codrive.modules.record.model;

import java.util.List;

import io.driver.codrive.modules.codeblock.model.CodeblockDetailResponse;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordDetailResponse(
	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	Long recordId,

	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	String title,

	@Schema(description = "난이도", example = "1")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]")
	List<String> tags,

	@Schema(description = "문제 플랫폼", example = "BAEKJOON")
	Platform platform,

	@Schema(description = "문제 URL", example = "PROBLEM_URL")
	String problemUrl,

	@Schema(description = "작성한 코드 블록", implementation = CodeblockDetailResponse.class)
	List<CodeblockDetailResponse> codeblocks
) {
	public static List<RecordDetailResponse> of(List<Record> records) {
		return records.stream().map(RecordDetailResponse::of).toList();
	}

	public static RecordDetailResponse of(Record record) {
		return RecordDetailResponse.builder()
			.title(record.getTitle())
			.level(record.getLevel())
			.tags(record.getTags())
			.platform(record.getPlatform())
			.problemUrl(record.getProblemUrl())
			.codeblocks(record.getCodeblocks().stream().map(CodeblockDetailResponse::of).toList())
			.build();
	}
}
