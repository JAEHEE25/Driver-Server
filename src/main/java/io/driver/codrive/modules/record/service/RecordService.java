package io.driver.codrive.modules.record.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;
import io.driver.codrive.modules.codeblock.model.request.CodeblockModifyRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordModifyRequest;
import io.driver.codrive.modules.record.model.request.RecordSaveRequest;
import io.driver.codrive.modules.record.model.request.RecordTempRequest;
import io.driver.codrive.modules.record.model.response.*;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {
	private static final int TEMP_RECORD_LIMIT = 3;
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordCategoryMappingService recordCategoryMappingService;
	private final CountBoardService countBoardService;
	private final RecordRepository recordRepository;

	@Transactional
	public RecordCreateResponse createSavedRecord(RecordSaveRequest recordRequest) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Record createdRecord = recordRepository.save(recordRequest.toSavedRecord(user));
		recordCategoryMappingService.createRecordCategoryMapping(recordRequest.tags(), createdRecord);
		user.addRecord(createdRecord);
		createCodeblocks(recordRequest.codeblocks(), createdRecord);
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
		if (record.compareRecordStatus(RecordStatus.TEMP)) {
			recordRepository.delete(record);
		}
		return response;
	}

	@Transactional
	public RecordCreateResponse createTempRecord(RecordTempRequest recordRequest) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		checkTempRecordLimit(user);

		Record createdRecord = recordRepository.save(recordRequest.toTempRecord(user));
		if (recordRequest.codeblocks() != null) {
			createCodeblocks(recordRequest.codeblocks(), createdRecord);
		}
		if (recordRequest.tags() != null) {
			recordCategoryMappingService.createRecordCategoryMapping(recordRequest.tags(), createdRecord);
		}
		return RecordCreateResponse.of(createdRecord);
	}

	private void validatePageAndSize(int page, int size) {
		if (page < 0 || size < 0) {
			throw new IllegalArgumentApplicationException("페이지 정보가 올바르지 않습니다.");
		}
	}

	private void checkTempRecordLimit(User user) {
		List<Record> tempRecords = recordRepository.findAllByUserAndRecordStatus(user, RecordStatus.TEMP);
		if (tempRecords.size() >= TEMP_RECORD_LIMIT) {
			throw new IllegalArgumentApplicationException("임시 저장 최대 개수를 초과했습니다.");
		}
	}

	@Transactional
	public TempRecordListResponse getTempRecordsByPage(int page, int size) {
		validatePageAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size);
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Page<Record> records = recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(user, RecordStatus.TEMP, pageable);
		return TempRecordListResponse.of(records.getTotalPages(), records);
	}

	@Transactional
	public RecordDayListResponse getRecordsByDay(Long userId, String pivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivot = DateUtils.getPivotDateOrToday(pivotDate);
		List<Record> records = recordRepository.getDailyRecords(user.getUserId(), pivot);
		return RecordDayListResponse.of(records);
	}

	@Transactional
	public RecordMonthListResponse getRecordsByMonth(Long userId, String requestPivotDate, Integer page, Integer size) {
		validatePageAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size);
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		Page<Record> records = recordRepository.getMonthlyRecords(user.getUserId(), pivotDate, pageable);
		return RecordMonthListResponse.of(records.getTotalPages(), records);
	}

	@Transactional
	public RecordCountBoardResponse getRecordsCount(Long userId, Period period, String requestPivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		List<RecordCountBoardResponse.RecordCountResponse> records = countBoardService.getCountBoard(user, period, pivotDate);
		return RecordCountBoardResponse.of(records);
	}

	@Transactional
	public UnsolvedMonthResponse getUnsolvedMonths(Long userId, String pivotDate) {
		RecordCountBoardResponse response = getRecordsCount(userId, null, pivotDate);
		List<Integer> unsolvedMonths = response.board().stream()
			.filter(data -> data.count() == 0)
			.map(data -> Integer.valueOf(data.date()))
			.collect(Collectors.toList());
		return UnsolvedMonthResponse.of(unsolvedMonths);
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

}
