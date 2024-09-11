package io.driver.codrive.modules.record.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.util.CalculateUtils;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.model.request.CodeblockModifyRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordModifyRequest;
import io.driver.codrive.modules.record.model.response.*;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordCategoryMappingService recordCategoryMappingService;
	private final RecordRepository recordRepository;

	@Transactional
	protected void updateSuccessRate(User user) {
		int solvedDayCountByWeek = recordRepository.getSolvedDaysByWeek(user.getUserId(), LocalDate.now());
		int successRate = CalculateUtils.calculateSuccessRate(solvedDayCountByWeek);
		user.changeSuccessRate(successRate);
	}

	@Transactional
	public int getRecordsCountByWeek(User user, LocalDate pivotDate) {
		return recordRepository.getRecordCountByWeek(user.getUserId(), pivotDate);
	}

	@Transactional
	public int getTodayRecordCount(User user) {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); //오늘 00:00:00
		LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59); //오늘 23:59:59
		return recordRepository.findAllByUserAndRecordStatusAndCreatedAtBetween(user, RecordStatus.SAVED, startOfDay, endOfDay).size();
	}

	@Transactional
	public Record getRecordById(Long recordId) {
		return recordRepository.findById(recordId).orElseThrow(() -> new NotFoundApplcationException("문제 풀이 데이터"));
	}

	@Transactional
	public RecordDetailResponse getRecordDetail(Long recordId) {
		Record record = getRecordById(recordId);
		RecordDetailResponse response = RecordDetailResponse.of(record);
		if (record.compareRecordStatus(RecordStatus.TEMP)) {
			recordRepository.delete(record);
		}
		return response;
	}

	@Transactional
	public TempRecordListResponse getTempRecordsByPage(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		PageUtils.validatePageable(pageable);
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Page<Record> records = recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(user, RecordStatus.TEMP, pageable);
		return TempRecordListResponse.of(records.getTotalPages(), records);
	}

	@Transactional
	public RecordModifyResponse modifyRecord(Long recordId, RecordModifyRequest request) {
		Record record = getRecordById(recordId);
		AuthUtils.checkOwnedEntity(record);
		updateRecord(record, request);
		return RecordModifyResponse.of(record);
	}

	@Transactional
	public void updateRecord(Record record, RecordModifyRequest request) {
		Record newRecord = request.toSavedRecord();
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
		AuthUtils.checkOwnedEntity(record);
		recordRepository.delete(record);
		updateSuccessRate(record.getUser());
	}

	@Transactional
	public void updateCodeblocks(Record record, List<CodeblockModifyRequest> requests) {
		List<Codeblock> codeblocks = CodeblockModifyRequest.of(requests, record);
		codeblockService.deleteCodeblock(record.getCodeblocks(), record);
		codeblockService.createCodeblock(codeblocks, record);
	}

	@Transactional
	public void updateCategories(Record record, List<String> tags) {
		if (!record.compareTags(tags)) {
			recordCategoryMappingService.deleteRecordCategoryMapping(record.getRecordCategoryMappings(), record);
			recordCategoryMappingService.createRecordCategoryMapping(tags, record);
		}
	}

	@Transactional
	public RecordRecentListResponse getRecentRecords(Long userId) {
		User user = userService.getUserById(userId);
		List<Record> records = recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(user, RecordStatus.SAVED);
		return RecordRecentListResponse.of(records);
	}

}
