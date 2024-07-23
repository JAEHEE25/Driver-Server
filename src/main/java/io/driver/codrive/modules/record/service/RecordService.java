package io.driver.codrive.modules.record.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.service.CodeblockService;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.recordTagMapping.domain.RecordTagMapping;
import io.driver.codrive.modules.mappings.recordTagMapping.service.RecordTagMappingService;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.RecordCreateRequest;
import io.driver.codrive.modules.record.model.RecordCreateResponse;
import io.driver.codrive.modules.tag.domain.Tag;
import io.driver.codrive.modules.tag.service.TagService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {
	private final TagService tagService;
	private final UserService userService;
	private final CodeblockService codeblockService;
	private final RecordTagMappingService recordTagMappingService;
	private final RecordRepository recordRepository;

	@Transactional
	public RecordCreateResponse createRecord(RecordCreateRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<Codeblock> codeblocks = request.codeblocks();
		Record savedRecord = recordRepository.save(request.toEntity(user));
		codeblockService.createCodeblock(codeblocks, savedRecord);

		List<RecordTagMapping> mappings = new ArrayList<>();
		request.tags().forEach(tag -> {
			Tag newTag = tagService.getTagByName(tag);
			mappings.add(RecordTagMapping.toEntity(savedRecord, newTag));
		});

		recordTagMappingService.createRecordTagMapping(mappings, savedRecord);
		return RecordCreateResponse.of(savedRecord);
	}
}
