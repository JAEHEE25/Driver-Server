package io.driver.codrive.modules.record.model.request;

import java.util.List;

import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;

import lombok.Getter;

@Getter
public abstract class RecordCreateRequest {
	String title;
	int level;
	List<String> tags;
	String platform;
	String problemUrl;
	List<CodeblockCreateRequest> codeblocks;
}
