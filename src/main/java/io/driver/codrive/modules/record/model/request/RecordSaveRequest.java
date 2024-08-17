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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RecordSaveRequest(
	@Schema(description = "문제 풀이 제목", example = "문제 풀이 제목")
	@NotBlank(message = "문제 풀이 제목을 입력해주세요.")
	String title,

	@Schema(description = "난이도", example = "1")
	@Range(min = 1, max = 5, message = "난이도는 {min}부터 {max}까지 선택해주세요.")
	int level,

	@Schema(description = "문제 유형 태그 (최대 2개)", example = "[\"완전탐색\"]",
		allowableValues = {"해시", "스택/큐", "힙 (Heap)", "정렬", "완전탐색", "탐욕법 (Greedy)",
			"동적계획법 (Dynamic Programming)", "깊이 우선 탐색 (DFS)", "너비 우선 탐색 (BFS)", "이분탐색",
			"그래프", "트리", "투포인터"})
	@NotNull(message = "문제 유형 태그를 선택해주세요.")
	@Size(min = 1, max = 2, message = "문제 유형 태그는 {min}개 이상 {max}개 이하로 선택해주세요.")
	List<String> tags,

	@Schema(description = "문제 플랫폼", example = "백준")
	@NotNull(message = "문제 플랫폼을 선택해주세요.")
	String platform,

	@Schema(description = "문제 URL", example = "https://codrive.co.kr")
	@Pattern(regexp = "^(https?|ftp)://[\\w.-]+(:[0-9]+)?(/([\\w/_.]*)?)?$",
		message = "URL 형식이 올바르지 않습니다.")
	@NotBlank(message = "문제 URL을 입력해주세요.")
	String problemUrl,

	@Schema(description = "작성한 코드 블록", implementation = CodeblockCreateRequest.class,
		example = "[{\"code\": \"CODE\", \"memo\": \"MEMO\"}]")
	@NotNull(message = "코드 블록을 입력해주세요.")
	@Size(min = 1, max = 10, message = "코드 블록은 {min}개 이상 {min}개 이하로 입력해주세요.")
	List<CodeblockCreateRequest> codeblocks

) {
	public Record toSavedRecord(User user) {
		return Record.builder()
			.user(user)
			.title(title)
			.level(level)
			.recordCategoryMappings(new ArrayList<>())
			.platform(Platform.getPlatformByName(platform))
			.problemUrl(problemUrl)
			.codeblocks(new ArrayList<>())
			.recordStatus(RecordStatus.SAVED)
			.build();
	}
}
