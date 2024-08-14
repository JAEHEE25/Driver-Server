package io.driver.codrive.modules.room.model;

import org.springframework.data.domain.Sort;

public enum SortType {
	NEW, DICT;

	public static Sort getSort(SortType sortType) {
		if (sortType == NEW) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		} else { //DICT
			return Sort.by(Sort.Direction.ASC, "title");
		}
	}

}