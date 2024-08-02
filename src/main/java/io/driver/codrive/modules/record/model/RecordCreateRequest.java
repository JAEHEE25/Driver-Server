package io.driver.codrive.modules.record.model;

import java.util.List;

import io.driver.codrive.modules.codeblock.model.CodeblockCreateRequest;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.user.domain.User;

public interface RecordCreateRequest {
	String getTitle();
	int getLevel();
	List<String> getTags();
	Platform getPlatform();
	String getProblemUrl();
	List<CodeblockCreateRequest> getCodeblocks();
	Record toEntity(User user);
}
