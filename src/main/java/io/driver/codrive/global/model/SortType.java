package io.driver.codrive.global.model;

import java.util.Comparator;

import org.springframework.data.domain.Sort;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.room.model.response.RoomParticipantItemDto;

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

	public static Comparator<RoomParticipantItemDto> getParticipantComparator(SortType sortType) {
		if (sortType == NEW) {
			return Comparator.comparing(RoomParticipantItemDto::createdAt).reversed();
		} else if (sortType == OLD) {
			return Comparator.comparing(RoomParticipantItemDto::createdAt);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

}