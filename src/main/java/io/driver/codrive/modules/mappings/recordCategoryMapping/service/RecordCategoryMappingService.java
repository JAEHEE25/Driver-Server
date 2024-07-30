package io.driver.codrive.modules.mappings.recordCategoryMapping.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMapping;
import io.driver.codrive.modules.mappings.recordCategoryMapping.domain.RecordCategoryMappingRepository;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.category.domain.Category;
import io.driver.codrive.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordCategoryMappingService {
	private final CategoryService categoryService;
	private final RecordCategoryMappingRepository recordCategoryMappingRepository;

	@Transactional
	public void createRecordCategoryMapping(List<RecordCategoryMapping> mappings, Record record) {
		recordCategoryMappingRepository.saveAll(mappings);
		record.changeCategories(mappings);
	}

	@Transactional
	public void deleteRecordCategoryMapping(List<RecordCategoryMapping> mappings, Record record) {
		recordCategoryMappingRepository.deleteAll(mappings);
		record.deleteCategories(mappings);
	}

	public List<RecordCategoryMapping> getRecordCategoryMappingsByRequest(List<String> tags, Record record) {
		List<RecordCategoryMapping> recordCategoryMappings = new ArrayList<>();
		tags.forEach(request -> {
			Category category = categoryService.getCategoryByName(request);
			recordCategoryMappings.add(RecordCategoryMapping.toEntity(record, category));
		});
		return recordCategoryMappings;
	}
}
