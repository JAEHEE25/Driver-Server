package io.driver.codrive.modules.language.service;

import org.springframework.stereotype.Service;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.language.domain.LanguageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LanguageService {
	private final LanguageRepository languageRepository;

	public Language getLanguageByName(String name) {
		return languageRepository.findByName(name).orElseThrow(() -> new IllegalArgumentApplicationException("지원하지 않는 언어입니다."));
	}

}
