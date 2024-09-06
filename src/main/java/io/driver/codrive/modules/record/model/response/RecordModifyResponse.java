package io.driver.codrive.modules.record.model.response;

import java.util.List;

import io.driver.codrive.modules.codeblock.model.response.CodeblockDetailResponse;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordModifyResponse(
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

	@Schema(description = "작성한 코드 블록", implementation = CodeblockDetailResponse.class)
	List<CodeblockDetailResponse> codeblocks
) {
	public static RecordModifyResponse of(Record record) {
		return RecordModifyResponse.builder()
			.title(record.getTitle())
			.level(record.getLevel())
			.tags(record.getCategories())
			.platform(record.getPlatformName())
			.problemUrl(record.getProblemUrl())
			.codeblocks(record.getCodeblocks().stream().map(CodeblockDetailResponse::of).toList())
			.build();
	}
}
