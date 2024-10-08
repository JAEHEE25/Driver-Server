package io.driver.codrive.modules.mappings.recordCategoryMapping.service;

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

import io.driver.codrive.modules.category.domain.Category;
import io.driver.codrive.modules.category.service.CategoryService;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMapping;
import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMappingRepository;
import io.driver.codrive.modules.record.domain.Platform;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordStatus;
import io.driver.codrive.modules.user.domain.User;

@ExtendWith(MockitoExtension.class)
class RecordCategoryMappingServiceTest {

	@InjectMocks
	private RecordCategoryMappingService recordCategoryMappingService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private RecordCategoryMappingRepository recordCategoryMappingRepository;

	private Record mockRecord;

	private final User mockUser = User.builder()
		.userId(1L)
		.language(Language.builder()
			.languageId(2L)
			.name("Java")
			.build())
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
	@DisplayName("RecordCategoryMapping 객체 생성 성공")
	void createRecordCategoryMapping_success() {
		// given
		String tag1 = "완전탐색";
		String tag2 = "구현";
		when(categoryService.getCategoryByName(tag1)).thenReturn(Category.builder().name(tag1).build());
		when(categoryService.getCategoryByName(tag2)).thenReturn(Category.builder().name(tag2).build());
		when(recordCategoryMappingRepository.saveAll(any())).thenReturn(new ArrayList<>());
		List<String> tags = List.of(tag1, tag2);

		// when
		recordCategoryMappingService.createRecordCategoryMapping(tags, mockRecord);

		// then
		assertEquals(tags, mockRecord.getCategories());
	}

	@Test
	@DisplayName("RecordCategoryMapping 객체 삭제 성공")
	void deleteRecordCategoryMapping_success() {
		// given
		Category category1 = Category.builder().name("완전탐색").build();
		Category category2 = Category.builder().name("구현").build();
		mockRecord.getRecordCategoryMappings().add(RecordCategoryMapping.builder().category(category1).build());
		mockRecord.getRecordCategoryMappings().add(RecordCategoryMapping.builder().category(category2).build());
		doNothing().when(recordCategoryMappingRepository).deleteAll(any());

		// when
		recordCategoryMappingService.deleteRecordCategoryMapping(mockRecord.getRecordCategoryMappings(), mockRecord);

		// then
		assertTrue(mockRecord.getCategories().isEmpty());

	}
}