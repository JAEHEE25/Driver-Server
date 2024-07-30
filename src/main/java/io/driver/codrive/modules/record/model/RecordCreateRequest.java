package io.driver.codrive.modules.record.model;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record RecordCreateRequest(
	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	String title,

	@Schema(description = "난이도", example = "1")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]",
		allowableValues = {"해시", "스택/큐", "힙 (Heap)", "정렬", "완전탐색", "탐욕법 (Greedy)",
			"동적계획법 (Dynamic Programming)", "깊이 우선 탐색 (DFS)", "너비 우선 탐색 (BFS)", "이분탐색",
			"그래프", "트리", "투포인터"})
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
	public Record toEntity(User user) {
		return Record.builder()
			.user(user)
			.title(title)
			.level(level)
			.recordTagMappings(new ArrayList<>())
			.platform(platform)
			.problemUrl(problemUrl)
			.codeblocks(codeblocks)
			.build();
	}
}