package io.driver.codrive.modules.mappings.recordTagMapping.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMapping;
import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMappingRepository;
import io.driver.codrive.modules.record.domain.Record;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordTagMappingService {
	private final RecordTagMappingRepository recordTagMappingRepository;

	@Transactional
	public void createRecordTagMapping(List<RecordTagMapping> mappings, Record record) {
		recordTagMappingRepository.saveAll(mappings);
		record.changeTags(mappings);
	}
}
