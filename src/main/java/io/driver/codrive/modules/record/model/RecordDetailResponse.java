package io.driver.codrive.modules.record.model;

import java.util.List;

import io.driver.codrive.modules.codeblock.model.CodeblockDetailResponse;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import lombok.Builder;

@Builder
public record RecordDetailResponse(
	String title,
	int level,
	List<String> tags,
	Platform platform,
	String problemUrl,
	List<CodeblockDetailResponse> codeblocks
) {
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
