package io.driver.codrive.global.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {
	public static void validatePageable(int page, int size) {
		if (page < 0 || size < 0) {
			throw new IllegalArgumentApplicationException("페이지 정보가 올바르지 않습니다.");
		}
	}

	public static <T> Page<T> getPage(List<T> content, Pageable pageable, int totalElements) {
        int start = (int) pageable.getOffset();

		if (start >= totalElements) {
            return new PageImpl<>(List.of(), pageable, totalElements);
        }

		int end = Math.min(start + pageable.getPageSize(), totalElements);
		List<T> pageContent = content.subList(start, end);
        return new PageImpl<>(pageContent, pageable, totalElements);
	}
}
