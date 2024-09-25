package io.driver.codrive.modules.record.model.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;

import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RecordTempRequest extends RecordCreateRequest {
	@Schema(description = "임시저장된 문제 풀이 ID (nullable)", example = "1")
	private Long tempRecordId;

	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	@NotBlank(message = "문제 풀이 제목을 입력해주세요.")
	private String title;

	@Schema(description = "난이도", example = "1")
	@Range(min = 1, max = 5, message = "난이도는 {min}부터 {max}까지 선택해주세요.")
	private int level;

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]",
		allowableValues = {"해시", "스택/큐", "힙 (Heap)", "정렬", "완전탐색", "탐욕법 (Greedy)",
			"동적계획법 (Dynamic Programming)", "깊이 우선 탐색 (DFS)", "너비 우선 탐색 (BFS)", "이분탐색",
			"그래프", "트리", "투포인터"})
	@Size(max = 2, message = "문제 유형 태그는 {max}개 이하로 선택해주세요.")
	private List<String> tags;

	@Schema(description = "문제 플랫폼", example = "백준")
	private String platform;

	@Schema(description = "문제 URL", example = "https://codrive.co.kr")
	private String problemUrl;

	@Schema(description = "작성한 코드 블록", implementation = CodeblockCreateRequest.class,
		example = "[{\"code\": \"CODE\", \"memo\": \"MEMO\"}]")
	@Size(max = 10, message = "코드 블록은 {min}개 이하로 입력해주세요.")
	private List<CodeblockCreateRequest> codeblocks;

    public Record toRecord(User user) {
		return Record.builder()
			.user(user)
			.title(title)
			.level(level)
			.recordCategoryMappings(new ArrayList<>())
			.platform(getPlatformByName())
			.problemUrl(problemUrl)
			.codeblocks(new ArrayList<>())
			.recordStatus(RecordStatus.TEMP)
			.build();
	}

    public Platform getPlatformByName() {
        if (platform == null || platform.isEmpty()) return null;
        return Platform.getPlatformByName(platform);
    }
}
