package io.driver.codrive.modules.record.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.util.CalculateUtils;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.model.request.CodeblockModifyRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordModifyRequest;
import io.driver.codrive.modules.record.model.response.*;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordCategoryMappingService recordCategoryMappingService;
	private final GithubCommitService githubCommitService;
	private final RecordRepository recordRepository;

	public Record getRecordById(Long recordId) {
		return recordRepository.findById(recordId).orElseThrow(() -> new NotFoundApplicationException("문제 풀이 데이터"));
	}

	public Long getOwnerIdByRecordId(Long recordId) {
		return recordRepository.findOwnerIdByRecordId(recordId);
	}

	@Transactional(readOnly = true)
	public RecordDetailResponse getRecordDetail(Long recordId) {
		Record record = getRecordById(recordId);
		return RecordDetailResponse.of(record);
	}

	public TempRecordListResponse getTempRecordsByPage(User user, int page, int size) {
		PageUtils.validatePageable(page, size);
		Pageable pageable = PageRequest.of(page, size);
		Page<Record> records = recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(user, RecordStatus.TEMP, pageable);
		return TempRecordListResponse.of(records.getTotalPages(), records);
	}

	@Transactional
	@PreAuthorize("@recordAccessHandler.isOwner(#recordId)")
	public RecordModifyResponse modifyRecord(Long userId, Long recordId, RecordModifyRequest request) throws IOException {
		User user = userService.getUserById(userId);
		Record record = getRecordById(recordId);
		deleteGithubContent(record, user);
		updateRecord(record, request);
		commitNewGithubContent(record, user);
		return RecordModifyResponse.of(record);
	}

	private void deleteGithubContent(Record record, User user) {
		String path = githubCommitService.getPath(record, record.getRecordNum());
		String sha = githubCommitService.getGithubContentSha(user, path);
		githubCommitService.deleteGithubContent(record, user, path, sha);
	}

	private void commitNewGithubContent(Record record, User user) throws IOException {
		String newPath = githubCommitService.getPath(record, record.getRecordNum());
		githubCommitService.commitToGithub(record, user, newPath);
	}

	protected void updateRecord(Record record, RecordModifyRequest request) {
		Record newRecord = request.toSavedRecord();
		record.changeTitle(newRecord.getTitle());
		record.changeLevel(newRecord.getLevel());
		record.changePlatform(newRecord.getPlatform());
		record.changeProblemUrl(newRecord.getProblemUrl());
		updateCodeblocks(record, request.codeblocks());
		updateCategories(record, request.tags());
	}

	protected void updateCodeblocks(Record record, List<CodeblockModifyRequest> requests) {
		codeblockService.deleteCodeblock(record.getCodeblocks(), record);
		List<Codeblock> codeblocks = CodeblockModifyRequest.of(requests, record);
		codeblockService.createCodeblock(codeblocks, record);
	}

	protected void updateCategories(Record record, List<String> tags) {
		if (!record.compareTags(tags)) {
			recordCategoryMappingService.deleteRecordCategoryMapping(record.getRecordCategoryMappings(), record);
			recordCategoryMappingService.createRecordCategoryMapping(tags, record);
		}
	}

	@Transactional
	@PreAuthorize("@recordAccessHandler.isOwner(#recordId)")
	public void deleteRecord(Long recordId) {
		Record record = getRecordById(recordId);
		recordRepository.delete(record);
		updateSuccessRate(record.getUser());
	}

	@Transactional
	protected void updateSuccessRate(User user) {
		int solvedDayCountByWeek = recordRepository.getSolvedDaysByWeek(user.getUserId(), LocalDate.now());
		int successRate = CalculateUtils.calculateSuccessRate(solvedDayCountByWeek);
		user.changeSuccessRate(successRate);
	}

	@Transactional(readOnly = true)
	public RecordRecentListResponse getRecentRecords(Long userId) {
		User user = userService.getUserById(userId);
		List<Record> records = recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(user, RecordStatus.SAVED);
		return RecordRecentListResponse.of(records);
	}

	public int getRecordsCountByWeek(Long userId, LocalDate pivotDate) {
		return recordRepository.getRecordsCountByWeek(userId, pivotDate);
	}

	public int getTodayRecordCount(User user) {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); //오늘 00:00:00
		LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59); //오늘 23:59:59
		return recordRepository.findAllByUserAndRecordStatusAndCreatedAtBetween(user, RecordStatus.SAVED, startOfDay, endOfDay).size();
	}
}
