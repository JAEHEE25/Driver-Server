package io.driver.codrive.modules.mappings.recordTagMapping.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMapping;
import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMappingRepository;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.tag.domain.Tag;
import io.driver.codrive.modules.tag.service.TagService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordTagMappingService {
	private final TagService tagService;
	private final RecordTagMappingRepository recordTagMappingRepository;

	@Transactional
	public void createRecordTagMapping(List<RecordTagMapping> mappings, Record record) {
		recordTagMappingRepository.saveAll(mappings);
		record.changeTags(mappings);
	}

	@Transactional
	public void deleteRecordTagMapping(List<RecordTagMapping> mappings) {
		recordTagMappingRepository.deleteAll(mappings);
	}

	public List<RecordTagMapping> getRecordTagMappingsByRequest(List<String> requestTags, Record record) {
		List<RecordTagMapping> recordTagMappings = new ArrayList<>();
		requestTags.forEach(request -> {
			Tag tag = tagService.getTagByName(request);
			recordTagMappings.add(RecordTagMapping.toEntity(record, tag));
		});
		return recordTagMappings;
	}

}
