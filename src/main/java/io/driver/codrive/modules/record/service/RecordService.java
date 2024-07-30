package io.driver.codrive.modules.record.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMapping;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
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
	public RecordCreateResponse createRecord(RecordCreateRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Record savedRecord = recordRepository.save(request.toEntity(user));
		codeblockService.createCodeblock(request.codeblocks(), savedRecord);

		List<RecordCategoryMapping> mappings = recordCategoryMappingService
			.getRecordCategoryMappingsByRequest(request.tags(), savedRecord);
		recordCategoryMappingService.createRecordCategoryMapping(mappings, savedRecord);
		return RecordCreateResponse.of(savedRecord);
	}

	@Transactional
	public Record getRecordById(Long recordId) {
		return recordRepository.findById(recordId).orElseThrow(() -> new NotFoundApplcationException("문제 풀이 데이터"));
	}

	@Transactional
	public RecordDetailResponse getRecordDetail(Long recordId) {
		Record record = getRecordById(recordId);
		return RecordDetailResponse.of(record);
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
		updateCodeblocks(record, newRecord);
		updateTags(record, request.tags());
	}

	@Transactional
	public void updateCodeblocks(Record record, Record newRecord) {
		codeblockService.deleteCodeblock(record.getCodeblocks());
		List<Codeblock> newCodeblocks = newRecord.getCodeblocks();
		codeblockService.createCodeblock(newCodeblocks, record);
	}

	@Transactional
	public void updateTags(Record record, List<String> tags) {
		if (record.getCategories() != tags) {
			recordCategoryMappingService.deleteRecordCategoryMapping(record.getRecordCategoryMappings(), record);
			List<RecordCategoryMapping> newMappings = recordCategoryMappingService.getRecordCategoryMappingsByRequest(tags, record);
			recordCategoryMappingService.createRecordCategoryMapping(newMappings, record);
		}
	}

}
