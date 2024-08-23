package io.driver.codrive.global.model;


import org.springframework.data.domain.Sort;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;

public enum SortType {
	NEW, DICT, OLD;

	public static Sort getRoomSort(SortType sortType) {
		if (sortType == NEW) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		} else if (sortType == DICT) {
			return Sort.by(Sort.Direction.ASC, "title");
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

	public static Sort getMemberSort(SortType sortType) {
		if (sortType == NEW) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		} else if (sortType == DICT) {
			return Sort.by(Sort.Direction.ASC, "user.nickname");
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

	// public static Sort getFollowingSort(SortType sortType) {
	// 	if (sortType == NEW) {
	// 		return Sort.by(Sort.Direction.DESC, "records.createdAt");
	// 	} else if (sortType == DICT) {
	// 		return Sort.by(Sort.Direction.ASC, "nickname");
	// 	} else {
	// 		throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
	// 	}
	// }

	public static Sort getRoomRequestSort(SortType sortType) {
		if (sortType == NEW) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		} else if (sortType == OLD) {
			return Sort.by(Sort.Direction.ASC, "createdAt");
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

}