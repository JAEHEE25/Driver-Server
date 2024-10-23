package io.driver.codrive.modules.record.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.codeblock.model.request.CodeblockCreateRequest;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.modules.mappings.recordCategoryMapping.service.RecordCategoryMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.request.RecordSaveRequest;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecordSaveService extends RecordCreateService<RecordSaveRequest> {
	private final RecordRepository recordRepository;
	private final GithubCommitService githubCommitService;

	public RecordSaveService(UserService userService, RecordRepository recordRepository,
		RecordCategoryMappingService recordCategoryMappingService, CodeblockService codeblockService,
		RecordService recordService, GithubCommitService githubCommitService) {
		super(userService, recordService, codeblockService, recordCategoryMappingService);
		this.recordRepository = recordRepository;
		this.githubCommitService = githubCommitService;
	}

	@Override
	@Transactional
	protected Record saveRecord(RecordSaveRequest recordRequest, User user) {
		Record createdRecord = recordRepository.save(recordRequest.toRecord(user));
		user.addRecord(createdRecord);
		deleteTempRecord(recordRequest.getTempRecordId());
		return createdRecord;
	}

	@Override
	boolean isSaveRecordRequest() {
		return true;
	}

	@Override
	@Transactional
	protected void createCodeblocks(List<CodeblockCreateRequest> codeblockRequests, Record record) {
		saveCodeblocks(codeblockRequests, record);
	}

	@Override
	@Transactional
	protected void createRecordCategoryMapping(List<String> tags, Record record) {
		saveRecordCategoryMapping(tags, record);
	}

	@Override
	protected void updateSolvedCount(User user) {
		user.addSolvedCount();
	}

	@Override
	@Transactional
	protected void updateSuccessRate(User user) {
		recordService.updateSuccessRate(user);
	}

	@Override
	@Transactional
	protected void updateJoinedRoomsLastUpdatedAt(Record createdRecord, User user) {
		user.getJoinedRooms().forEach(room -> room.changeLastUpdatedAt(createdRecord.getCreatedAt()));
	}

	@Override
	@Transactional
	protected void commitToGithub(Record createdRecord, User user) throws IOException {
		String path = githubCommitService.getPath(createdRecord, user.getSolvedCount());
		createdRecord.changeRecordNum(user.getSolvedCount());
		githubCommitService.commitToGithub(createdRecord, createdRecord.getUser(), path);
	}
}
