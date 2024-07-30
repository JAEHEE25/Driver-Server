package io.driver.codrive.modules.category.service;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.category.domain.Category;
import io.driver.codrive.modules.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public Category getCategoryByName(String name) {
		return categoryRepository.findByName(name).orElseThrow(() -> new IllegalArgumentApplicationException("지원하지 않는 문제 풀이 유형입니다."));
	}

}
