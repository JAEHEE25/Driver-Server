package io.driver.codrive.modules.record.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordTempRequest;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;

@Service
public class RecordTempService extends RecordCreateService<RecordTempRequest> {
	private static final int TEMP_RECORD_LIMIT = 3;
	private final RecordRepository recordRepository;

	public RecordTempService(UserService userService, RecordRepository recordRepository,
		RecordCategoryMappingService recordCategoryMappingService, CodeblockService codeblockService) {
		super(userService, codeblockService, recordCategoryMappingService);
		this.recordRepository = recordRepository;
	}

	@Override
	@Transactional
	protected Record saveRecord(RecordTempRequest recordRequest, User user) {
		checkTempRecordLimit(user);
		return recordRepository.save(recordRequest.toRecord(user));
	}

	@Override
	@Transactional
	protected void createCodeblocks(List<CodeblockCreateRequest> codeblockRequests, Record record) {
		if (codeblockRequests != null && !codeblockRequests.isEmpty()) {
			saveCodeblocks(codeblockRequests, record);
		}
	}

	@Override
	@Transactional
	protected void createRecordCategoryMapping(List<String> tags, Record record) {
		if (tags != null && !tags.isEmpty()) {
			saveRecordCategoryMapping(tags, record);
		}
	}

	@Override
	protected void updateSuccessRate(User user) {}

	@Override
	protected void updateJoinedRoomsLastUpdatedAt(Record createdRecord, User user) {}

	@Transactional
	protected void checkTempRecordLimit(User user) {
		List<Record> tempRecords = recordRepository.findAllByUserAndRecordStatus(user, RecordStatus.TEMP);
		if (tempRecords.size() >= TEMP_RECORD_LIMIT) {
			throw new IllegalArgumentApplicationException("임시 저장 최대 개수를 초과했습니다.");
		}
	}
}
