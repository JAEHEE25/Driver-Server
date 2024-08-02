package io.driver.codrive.modules.record.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.model.*;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.Status;
import io.driver.codrive.modules.record.model.*;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordCategoryMappingService recordCategoryMappingService;
	private final BoardService boardService;
	private final RecordRepository recordRepository;

	@Transactional
	public RecordCreateResponse createRecord(RecordCreateRequest recordRequest) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Record createdRecord = recordRepository.save(recordRequest.toEntity(user));
		createCodeblocks(recordRequest.getCodeblocks(), createdRecord);
		recordCategoryMappingService.createRecordCategoryMapping(recordRequest.getTags(), createdRecord);
		return RecordCreateResponse.of(createdRecord);
	}

	@Transactional
	public void createCodeblocks(List<CodeblockCreateRequest> codeblockRequests, Record record) {
		List<Codeblock> codeblocks = CodeblockCreateRequest.of(codeblockRequests, record);
		codeblockService.createCodeblock(codeblocks, record);
	}

	@Transactional
	public Record getRecordById(Long recordId) {
		return recordRepository.findById(recordId).orElseThrow(() -> new NotFoundApplcationException("문제 풀이 데이터"));
	}

	@Transactional
	public RecordDetailResponse getRecordDetail(Long recordId) {
		Record record = getRecordById(recordId);
		RecordDetailResponse response = RecordDetailResponse.of(record);
		if (record.getStatus() == Status.TEMP) {
			recordRepository.delete(record);
		}
		return response;
	}

	@Transactional
	public RecordListResponse getTempRecords() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<Record> records = recordRepository.findAllByUserAndStatus(user, Status.TEMP);
		return RecordListResponse.of(records);
	}

	@Transactional
	public RecordListResponse getRecords() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<Record> records = recordRepository.findAllByUser(user);
		return RecordListResponse.of(records);
	}

	@Transactional
	public RecordListResponse getRecordsByDate(Long userId, String pivotDate) {
		User user = userService.getUserById(userId);
		List<Record> records = boardService.getRecordsByDate(user, pivotDate);
		return RecordListResponse.of(records);
	}

	@Transactional
	public RecordBoardResponse getRecordsBoard(Long userId, Period period, String pivotDate) {
		User user = userService.getUserById(userId);
		List<BoardResponse> records = boardService.getBoardResponse(user, period, pivotDate);
		return RecordBoardResponse.of(records);
	}

	@Transactional
	public RecordModifyResponse modifyRecord(Long recordId, RecordModifyRequest request) {
		Record record = getRecordById(recordId);
		updateRecord(record, request);
		return RecordModifyResponse.of(record);
	}

	@Transactional
	public void updateRecord(Record record, RecordModifyRequest request) {
		Record newRecord = request.toEntity();
		record.changeTitle(newRecord.getTitle());
		record.changeLevel(newRecord.getLevel());
		record.changePlatform(newRecord.getPlatform());
		record.changeProblemUrl(newRecord.getProblemUrl());
		updateCodeblocks(record, request.codeblocks());
		updateCategories(record, request.tags());
	}

	@Transactional
	public void deleteRecord(Long recordId) {
		Record record = getRecordById(recordId);
		recordRepository.delete(record);
	}

	@Transactional
	public void updateCodeblocks(Record record, List<CodeblockModifyRequest> requests) {
		List<Codeblock> codeblocks = CodeblockModifyRequest.of(requests, record);
		codeblockService.deleteCodeblock(record.getCodeblocks(), record);
		codeblockService.createCodeblock(codeblocks, record);
	}

	@Transactional
	public void updateCategories(Record record, List<String> tags) {
		if (record.getCategories() != tags) {
			recordCategoryMappingService.deleteRecordCategoryMapping(record.getRecordCategoryMappings(), record);
			recordCategoryMappingService.createRecordCategoryMapping(tags, record);
		}
	}

}
