package io.driver.codrive.modules.record.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.model.request.RecordCreateRequest;
import io.driver.codrive.modules.record.model.response.RecordCreateResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RecordCreateService<T extends RecordCreateRequest> {
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordCategoryMappingService recordCategoryMappingService;

	@Transactional
	public RecordCreateResponse createRecord(T recordRequest) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Record createdRecord = saveRecord(recordRequest, user);

		createCodeblocks(recordRequest.getCodeblocks(), createdRecord);
		createRecordCategoryMapping(recordRequest.getTags(), createdRecord);
		if (isSaveRecordRequest()) { //등록일 경우
			updateSuccessRate(user);
			updateJoinedRoomsLastUpdatedAt(createdRecord, user);
		}
		return RecordCreateResponse.of(createdRecord);
	}

	boolean isSaveRecordRequest() {
		return false;
	}

	protected abstract Record saveRecord(T recordRequest, User user);

	protected abstract void createCodeblocks(List<CodeblockCreateRequest> codeblockRequests, Record record);

	@Transactional
	protected void saveCodeblocks(List<CodeblockCreateRequest> codeblockRequests, Record record) {
		List<Codeblock> codeblocks = CodeblockCreateRequest.of(codeblockRequests, record);
		codeblockService.createCodeblock(codeblocks, record);
	}

	protected abstract void createRecordCategoryMapping(List<String> tags, Record record);

	@Transactional
	protected void saveRecordCategoryMapping(List<String> tags, Record record) {
		recordCategoryMappingService.createRecordCategoryMapping(tags, record);
	}

	protected abstract void updateSuccessRate(User user);
	protected abstract void updateJoinedRoomsLastUpdatedAt(Record createdRecord, User user);
}
