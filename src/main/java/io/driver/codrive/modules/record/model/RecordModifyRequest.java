package io.driver.codrive.modules.record.model;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;

public record RecordModifyRequest(
	String title,
	int level,
	List<String> tags,
	Platform platform,
	String problemUrl,
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