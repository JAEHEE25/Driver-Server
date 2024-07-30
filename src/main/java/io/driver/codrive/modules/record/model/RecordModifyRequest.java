package io.driver.codrive.modules.record.model;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.swagger.v3.oas.annotations.media.Schema;

public record RecordModifyRequest(
	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	String title,

	@Schema(description = "난이도", example = "1")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]")
	List<String> tags,

	@Schema(description = "문제 플랫폼", example = "BAEKJOON", allowableValues = {"BAEKJOON", "PROGRAMMERS", "SWEA",
		"LEETCODE", "HACKERRANK", "OTHER"})
	Platform platform,

	@Schema(description = "문제 URL", example = "PROBLEM_URL")
	String problemUrl,

	@Schema(description = "작성한 코드 블록", example = """
		{
			"code" : "CODE",
			"memo": "MEMO"
		}
		""")
	List<Codeblock> codeblocks
) {
	public Record toEntity() {
		return Record.builder()
			.title(title)
			.level(level)
			.recordTagMappings(new ArrayList<>())
			.platform(platform)
			.problemUrl(problemUrl)
			.codeblocks(codeblocks)
			.build();
	}
}