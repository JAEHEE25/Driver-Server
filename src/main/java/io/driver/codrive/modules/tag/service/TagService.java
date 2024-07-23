package io.driver.codrive.modules.tag.service;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.tag.domain.Tag;
import io.driver.codrive.modules.tag.domain.TagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
	private final TagRepository tagRepository;

	public Tag getTagByName(String name) {
		return tagRepository.findByName(name).orElseThrow(() -> new IllegalArgumentApplicationException("지원하지 않는 문제 풀이 유형입니다."));
	}

}
