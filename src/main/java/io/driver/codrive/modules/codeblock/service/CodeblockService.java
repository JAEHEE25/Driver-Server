package io.driver.codrive.modules.codeblock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.codeblock.domain.Codeblock;
import io.driver.codrive.modules.codeblock.domain.CodeblockRepository;
import io.driver.codrive.modules.record.domain.Record;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeblockService {
	private final CodeblockRepository codeblockRepository;

	public void createCodeblock(List<Codeblock> codeblocks, Record record) {
		codeblocks.forEach(codeblock -> {
			codeblock.changeRecord(record);
			codeblockRepository.save(codeblock);
		});
	}
}
