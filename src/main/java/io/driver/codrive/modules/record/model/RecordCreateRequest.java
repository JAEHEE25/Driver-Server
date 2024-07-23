package io.driver.codrive.modules.record.model;

import java.util.ArrayList;
import java.util.List;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.user.domain.User;

public record RecordCreateRequest(
	String title,
	int difficulty,
	List<String> tags,
	Platform platform,
	String problemUrl,
	List<Codeblock> codeblocks
) {
	public Record toEntity(User user) {
		return Record.builder()
			.user(user)
			.title(title)
			.difficulty(difficulty)
			.recordTagMappings(new ArrayList<>())
			.platform(platform)
			.problemUrl(problemUrl)
			.codeblocks(codeblocks)
			.build();
	}
}