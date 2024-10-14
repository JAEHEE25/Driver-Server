package io.driver.codrive.modules.record.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.record.model.request.RecordSaveRequest;
import io.driver.codrive.modules.record.service.github.GithubCommitService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;

@ExtendWith(MockitoExtension.class)
class RecordSaveServiceTest {
	@InjectMocks
	private RecordSaveService recordSaveService;

	@Mock
	private RecordRepository recordRepository;

	@Mock
	private RecordService recordService;

	@Mock
	private GithubCommitService githubCommitService;

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
		.solvedCount(1L)
		.build();

	@BeforeEach
	void setRecord() {
		mockRecord = Record.builder()
			.recordId(1L)
			.title("title")
			.level(1)
			.platform(Platform.BAEKJOON)
			.problemUrl("https://www.acmicpc.net/problem/1000")
			.recordStatus(RecordStatus.SAVED)
			.user(mockUser)
			.codeblocks(new ArrayList<>())
			.recordCategoryMappings(new ArrayList<>())
			.build();
		ReflectionTestUtils.setField(mockRecord, "createdAt", LocalDateTime.now());
	}

	@Test
	@DisplayName("문제 풀이 등록 성공")
	void saveRecord_success() {
		 // given
		 RecordSaveRequest mockRecordRequest = mock(RecordSaveRequest.class);
		 when(mockRecordRequest.toRecord(mockUser)).thenReturn(mockRecord);
		 when(recordRepository.save(mockRecord)).thenReturn(mockRecord);

		// when
		Record createdRecord = recordSaveService.saveRecord(mockRecordRequest, mockUser);

		// then
		assertNotNull(createdRecord);
		assertEquals(RecordStatus.SAVED, createdRecord.getRecordStatus());
		assertEquals(mockUser.getRecords().size(), 1);
		assertEquals(mockRecord, createdRecord);
	}

	@Test
	@DisplayName("등록된 문제 풀이인지 확인 성공")
	void isSaveRecordRequest_success() {
		assertTrue(recordSaveService.isSaveRecordRequest());
	}

	@Test
	@DisplayName("그룹의 최근 문제 풀이 등록 시간 업데이트 성공")
	void updateJoinedRoomsLastUpdatedAt_success() {
		// given
		Room mockRoom = Room.builder()
			.roomId(1L)
			.roomUserMappings(new ArrayList<>())
			.build();
		RoomUserMapping mockRoomUserMapping = RoomUserMapping.builder().user(mockUser).room(mockRoom).build();
		mockUser.getRoomUserMappings().add(mockRoomUserMapping);

		// when
		recordSaveService.updateJoinedRoomsLastUpdatedAt(mockRecord, mockUser);

		// then
		assertEquals(mockUser.getJoinedRooms().get(0).getLastUpdatedAt(), mockRecord.getCreatedAt());
	}

	@Test
	@DisplayName("Github에 문제 풀이 등록 메서드 성공")
	void commitToGithub_success() throws IOException {
		// given
		when(githubCommitService.getPath(mockRecord, mockRecord.getRecordId())).thenReturn("path");
		doNothing().when(githubCommitService).commitToGithub(mockRecord, mockUser, "path", null);

		// when & then
		assertDoesNotThrow(() -> recordSaveService.commitToGithub(mockRecord, mockUser));
	}

    @Test
	@DisplayName("Github에 문제 풀이 등록 메서드 실패")
    void commitToGithub_fail_ioException() throws IOException {
		// given
		when(githubCommitService.getPath(mockRecord, mockRecord.getRecordId())).thenReturn("path");
        doThrow(new IOException("GitHub commit failed")).when(githubCommitService).commitToGithub(mockRecord, mockUser, "path", null);

		// when & then
        assertThrows(IOException.class, () -> recordSaveService.commitToGithub(mockRecord, mockUser));
    }
}