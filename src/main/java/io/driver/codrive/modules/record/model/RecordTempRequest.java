package io.driver.codrive.modules.record.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import io.driver.codrive.modules.codeblock.model.CodeblockCreateRequest;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.Status;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordTempRequest(
	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	@NotBlank
	String title,

	@Schema(description = "난이도", example = "1")
	@Range(min = 1, max = 5, message = "난이도는 {min}부터 {max}까지 선택해주세요.")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]",
		allowableValues = {"해시", "스택/큐", "힙 (Heap)", "정렬", "완전탐색", "탐욕법 (Greedy)",
			"동적계획법 (Dynamic Programming)", "깊이 우선 탐색 (DFS)", "너비 우선 탐색 (BFS)", "이분탐색",
			"그래프", "트리", "투포인터"})
	@Size(max = 2, message = "문제 유형 태그는 {max}개 이하로 선택해주세요.")
	List<String> tags,

	@Schema(description = "문제 플랫폼", example = "BAEKJOON", allowableValues = {"BAEKJOON", "PROGRAMMERS", "SWEA",
		"LEETCODE", "HACKERRANK", "OTHER"})
	Platform platform,

	@Schema(description = "문제 URL", example = "PROBLEM_URL")
	String problemUrl,

	@Schema(description = "작성한 코드 블록", implementation = CodeblockCreateRequest.class,
		example = "[{\"code\": \"CODE\", \"memo\": \"MEMO\"}]")
	@Size(max = 10, message = "코드 블록은 {min}개 이하로 입력해주세요.")
	List<CodeblockCreateRequest> codeblocks

) implements RecordCreateRequest {
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public Platform getPlatform() {
		return platform;
	}

	@Override
	public String getProblemUrl() {
		return problemUrl;
	}

	@Override
	public List<CodeblockCreateRequest> getCodeblocks() {
		return codeblocks;
	}

	@Override
	public Record toEntity(User user) {
		return Record.builder()
			.user(user)
			.title(getTitle())
			.level(getLevel())
			.recordCategoryMappings(new ArrayList<>())
			.platform(getPlatform())
			.problemUrl(getProblemUrl())
			.codeblocks(new ArrayList<>())
			.status(Status.TEMP)
			.build();
	}
}
