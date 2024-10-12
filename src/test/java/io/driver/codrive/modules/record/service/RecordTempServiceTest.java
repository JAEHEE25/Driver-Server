package io.driver.codrive.modules.record.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordTempRequest;
import io.driver.codrive.modules.user.domain.User;

@ExtendWith(MockitoExtension.class)
class RecordTempServiceTest {

	@InjectMocks
	private RecordTempService recordTempService;

	@Mock
	private RecordRepository recordRepository;

	private Record mockRecord;

	private final User mockUser = User.builder()
		.userId(1L)
		.language(Language.builder()
			.languageId(2L)
			.name("Java")
			.build())
		.successRate(0)
		.records(new ArrayList<>())
		.roomUserMappings(new ArrayList<>())
		.build();

	@BeforeEach
	void setRecord() {
		mockRecord = Record.builder()
			.recordId(1L)
			.title("title")
			.level(1)
			.platform(Platform.BAEKJOON)
			.problemUrl("https://www.acmicpc.net/problem/1000")
			.recordStatus(RecordStatus.TEMP)
			.user(mockUser)
			.codeblocks(new ArrayList<>())
			.recordCategoryMappings(new ArrayList<>())
			.build();
		ReflectionTestUtils.setField(mockRecord, "createdAt", LocalDateTime.now());
	}

	@Test
	@DisplayName("임시저장 성공")
	void saveRecord_success() {
		// given
		RecordTempRequest mockRecordRequest = mock(RecordTempRequest.class);
		when(mockRecordRequest.getTempRecordId()).thenReturn(null);
		when(mockRecordRequest.toRecord(mockUser)).thenReturn(mockRecord);
		when(recordRepository.save(mockRecord)).thenReturn(mockRecord);

		// when
		Record createdRecord = recordTempService.saveRecord(mockRecordRequest, mockUser);

		// then
		assertNotNull(createdRecord);
		assertEquals(RecordStatus.TEMP, createdRecord.getRecordStatus());
		assertEquals(mockRecord, createdRecord);
	}

	@Test
	@DisplayName("임시저장 실패_임시저장 최대 개수 초과")
	void saveRecord_fail_badRequest() {
		// given
		RecordTempRequest mockRecordRequest = mock(RecordTempRequest.class);
		when(mockRecordRequest.getTempRecordId()).thenReturn(null);
		when(recordRepository.findAllByUserAndRecordStatus(mockUser, RecordStatus.TEMP))
			.thenReturn(List.of(mockRecord, mockRecord, mockRecord));

		// when & then
		assertThrows(
			IllegalArgumentApplicationException.class, () -> recordTempService.saveRecord(mockRecordRequest, mockUser));
	}

}