package io.driver.codrive.global.model;


import static io.driver.codrive.modules.mappings.roomUserMapping.domain.QRoomUserMapping.*;
import static io.driver.codrive.modules.room.domain.QRoom.*;

import java.util.Comparator;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.room.domain.Room;

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

	public static Comparator<Room> getJoinedRoomComparator(SortType sortType) {
		if (sortType == SortType.NEW) {
			return Comparator.comparing(Room::getCreatedAt).reversed();
		} else if (sortType == SortType.DICT) {
			return Comparator.comparing(Room::getTitle);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

	public static Sort getRoomRequestSort(SortType sortType) {
		if (sortType == NEW) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		} else if (sortType == OLD) {
			return Sort.by(Sort.Direction.ASC, "createdAt");
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

	public OrderSpecifier createRoomUserOrderSpecifier(SortType sortType) {
		if (sortType == NEW) {
			return new OrderSpecifier<>(Order.DESC, roomUserMapping.createdAt);
		} else if (sortType == DICT) {
			return new OrderSpecifier<>(Order.ASC, room.title);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
    }

	public OrderSpecifier createRoomOrderSpecifier(SortType sortType) {
		if (sortType == NEW) {
			return new OrderSpecifier<>(Order.DESC, room.createdAt);
		} else if (sortType == DICT) {
			return new OrderSpecifier<>(Order.ASC, room.title);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
    }

}