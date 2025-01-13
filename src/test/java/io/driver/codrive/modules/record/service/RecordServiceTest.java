// package io.driver.codrive.modules.record.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.io.IOException;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
// import io.driver.codrive.global.exception.NotFoundApplicationException;
// import io.driver.codrive.global.util.DateUtils;
// import io.driver.codrive.modules.codeblock.service.CodeblockService;
// import io.driver.codrive.modules.language.domain.Language;
// import io.driver.codrive.modules.record.domain.Platform;
// import io.driver.codrive.modules.record.domain.Record;
// import io.driver.codrive.modules.record.domain.RecordRepository;
// import io.driver.codrive.modules.record.domain.RecordStatus;
// import io.driver.codrive.modules.record.model.request.RecordModifyRequest;
// import io.driver.codrive.modules.record.model.response.RecordDetailResponse;
// import io.driver.codrive.modules.record.model.response.RecordModifyResponse;
// import io.driver.codrive.modules.record.model.response.RecordRecentListResponse;
// import io.driver.codrive.modules.record.model.response.TempRecordListResponse;
// import io.driver.codrive.modules.record.service.github.GithubCommitService;
// import io.driver.codrive.modules.user.domain.User;
// import io.driver.codrive.modules.user.service.UserService;
//
// @ExtendWith(MockitoExtension.class)
// class RecordServiceTest {
// 	@InjectMocks
// 	private RecordService recordService;
//
// 	@Mock
// 	private RecordRepository recordRepository;
//
// 	@Mock
// 	private UserService userService;
//
// 	@Mock
// 	private GithubCommitService githubCommitService;
//
// 	@Mock
// 	private CodeblockService codeblockService;
//
// 	private Record mockRecord;
//
// 	private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
//
// 	private final User mockUser = User.builder()
// 		.userId(1L)
// 		.language(Language.builder()
// 			.languageId(2L)
// 			.name("Java")
// 			.build())
// 		.build();
//
// 	@BeforeEach
// 	void setRecord() {
// 		mockRecord = Record.builder()
// 			.recordId(1L)
// 			.title("title")
// 			.level(1)
// 			.platform(Platform.BAEKJOON)
// 			.problemUrl("https://www.acmicpc.net/problem/1000")
// 			.recordStatus(RecordStatus.SAVED)
// 			.user(mockUser)
// 			.codeblocks(new ArrayList<>())
// 			.recordCategoryMappings(new ArrayList<>())
// 			.build();
// 		ReflectionTestUtils.setField(mockRecord, "createdAt", LocalDateTime.now());
// 	}
//
// 	void setSecurityContext() {
// 		SecurityContext securityContext = mock(SecurityContext.class);
// 		Authentication authentication = mock(Authentication.class);
//
// 		mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
// 		mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
// 		when(securityContext.getAuthentication()).thenReturn(authentication);
// 		when(authentication.getPrincipal()).thenReturn(1L);
// 	}
//
// 	void tearDown() {
// 		mockedSecurityContextHolder.close();
// 	}
//
// 	@Test
// 	@DisplayName("recordId로 문제 풀이 조회 성공")
// 	void getRecordById_success() {
// 		//given
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
//
// 		//when
// 		Record record = recordService.getRecordById(recordId);
//
// 		//then
// 		assertNotNull(record);
// 		assertEquals(recordId, record.getRecordId());
// 	}
//
// 	@Test
// 	@DisplayName("recordId로 문제 풀이 조회 실패_문제 풀이가 존재하지 않을 경우")
// 	void getRecordById_faile_notFoundException() {
// 		//given
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.empty());
//
// 		//when & then
// 		assertThrows(NotFoundApplicationException.class, () -> recordService.getRecordById(recordId));
// 	}
//
// 	@Test
// 	@DisplayName("recordId로 문제 풀이 상세 조회 성공")
// 	void getRecordDetail_success() {
// 		//given
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
//
// 		//when
// 		RecordDetailResponse response = recordService.getRecordDetail(recordId);
//
// 		//then
// 		assertAll("RecordDetailResponse",
// 			() -> assertNotNull(response),
// 			() -> assertEquals(mockRecord.getRecordId(), response.recordId()),
// 			() -> assertEquals(mockRecord.getTitle(), response.title()),
// 			() -> assertEquals(mockRecord.getLevel(), response.level()),
// 			() -> assertEquals(mockRecord.getCategories(), response.tags()),
// 			() -> assertEquals(mockRecord.getPlatformName(), response.platform()),
// 			() -> assertEquals(mockRecord.getProblemUrl(), response.problemUrl()),
// 			() -> assertEquals(mockRecord.getCodeblocks().size(), response.codeblocks().size()),
// 			() -> assertEquals(DateUtils.formatCreatedAtByYMDHM(mockRecord.getCreatedAt()), response.createdAt())
// 		);
// 	}
//
// 	@Test
// 	@DisplayName("페이지별 임시 문제 풀이 조회 성공")
// 	void getTempRecordsByPage_success() {
// 		// given
// 		setSecurityContext();
// 		when(userService.getUserById(1L)).thenReturn(mockUser);
//
// 		int page = 0;
// 		int size = 1;
// 		when(recordRepository
// 			.findAllByUserAndRecordStatusOrderByCreatedAtDesc(mockUser, RecordStatus.TEMP, PageRequest.of(page, size)))
// 			.thenReturn(new PageImpl<>(List.of(mockRecord)));
//
// 		// when
// 		TempRecordListResponse response = recordService.getTempRecordsByPage(page, size);
//
// 		// then
// 		assertAll("TempRecordListResponse",
// 			() -> assertNotNull(response),
// 			() -> assertEquals(1, response.totalPage()),
// 			() -> assertEquals(1, response.records().size())
// 		);
//
// 		tearDown();
// 	}
//
// 	@Test
// 	@DisplayName("페이지별 임시 문제 풀이 조회 실패_페이지 정보가 올바르지 않을 경우")
// 	void getTempRecordsByPage_fail_badRequestException() {
// 		//given
// 		int page = -1;
// 		int size = 1;
//
// 		//when & then
// 		assertThrows(IllegalArgumentApplicationException.class, () -> recordService.getTempRecordsByPage(page, size));
// 	}
//
// 	@Test
// 	@DisplayName("문제 풀이 수정 성공")
// 	void modifyRecord_success() throws IOException {
// 		// given
// 		setSecurityContext();
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
//
// 		// when
// 		RecordModifyRequest request = new RecordModifyRequest("new_title", 2, new ArrayList<>(),
// 			"백준", "https://www.acmicpc.net/problem/1001", new ArrayList<>());
// 		RecordModifyResponse response = recordService.modifyRecord(1L, request);
//
// 		// then
// 		assertAll("RecordModifyResponse",
// 			() -> assertNotNull(response),
// 			() -> assertEquals(request.title(), response.title()),
// 			() -> assertEquals(request.level(), response.level()),
// 			() -> assertEquals(request.tags(), response.tags()),
// 			() -> assertEquals(request.platform(), response.platform()),
// 			() -> assertEquals(request.problemUrl(), response.problemUrl()),
// 			() -> assertEquals(request.codeblocks().size(), response.codeblocks().size())
// 		);
//
// 		tearDown();
// 	}
//
// 	@Test
// 	@DisplayName("문제 풀이 수정 실패_지원하지 않는 플랫폼일 경우")
// 	void modifyRecord_fail_badRequestException_platform() {
// 		// given
// 		setSecurityContext();
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
//
// 		// when
// 		RecordModifyRequest request = new RecordModifyRequest("new_title", 2, List.of("완전탐색"),
// 			"백준그래머스", "https://www.acmicpc.net/problem/1001", new ArrayList<>());
//
// 		//when & then
// 		assertThrows(IllegalArgumentApplicationException.class, () -> recordService.modifyRecord(1L, request));
//
// 		tearDown();
// 	}
//
// 	@Test
// 	@DisplayName("문제 풀이 삭제 성공")
// 	void deleteRecord_success() {
// 		// given
// 		setSecurityContext();
// 		Long recordId = 1L;
// 		when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
//
// 		// when
// 		recordService.deleteRecord(recordId);
//
// 		// then
// 		verify(recordRepository).delete(mockRecord);
//
// 		tearDown();
// 	}
//
// 	@Test
// 	@DisplayName("사용자 성과율 업데이트 성공")
// 	void updateSuccessRate() {
// 		// given
// 		when(recordRepository.getSolvedDaysByWeek(1L, LocalDate.now())).thenReturn(1);
// 		int expectedSuccessRate = 15;
//
// 		// when
// 		recordService.updateSuccessRate(mockUser);
//
// 		// then
// 		assertEquals(expectedSuccessRate, mockUser.getSuccessRate());
// 	}
//
// 	@Test
// 	@DisplayName("최근 문제 풀이 목록 조회 성공")
// 	void getRecentRecords_success() {
// 		// given
// 		Long userId = 1L;
// 		when(userService.getUserById(userId)).thenReturn(mockUser);
// 		when(recordRepository.findAllByUserAndRecordStatusOrderByCreatedAtDesc(mockUser, RecordStatus.SAVED))
// 			.thenReturn(List.of(mockRecord));
//
// 		// when
// 		RecordRecentListResponse response = recordService.getRecentRecords(userId);
//
// 		// then
// 		assertAll("RecordRecentListResponse",
// 			() -> assertNotNull(response),
// 			() -> assertEquals(1, response.records().size())
// 		);
// 	}
//
// 	@Test
// 	@DisplayName("특정 주에 작성한 문제 풀이 개수 조회")
// 	void getRecordsCountByWeek() {
// 		// given
// 		LocalDate pivotDate = LocalDate.now();
// 		when(recordRepository.getRecordsCountByWeek(mockUser.getUserId(), pivotDate)).thenReturn(1);
//
// 		// when
// 		int count = recordService.getRecordsCountByWeek(mockUser, pivotDate);
//
// 		// then
// 		assertEquals(1, count);
// 	}
//
// 	@Test
// 	@DisplayName("오늘 작성한 문제 풀이 개수 조회")
// 	void getTodayRecordCount() {
// 		// given
// 		LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); //오늘 00:00:00
// 		LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59); //오늘 23:59:59
// 		when(recordRepository.findAllByUserAndRecordStatusAndCreatedAtBetween(mockUser, RecordStatus.SAVED, startOfDay, endOfDay))
// 			.thenReturn(List.of(mockRecord));
//
// 		// when
// 		int count = recordService.getTodayRecordCount(mockUser);
//
// 		// then
// 		assertEquals(1, count);
// 	}
//
// }