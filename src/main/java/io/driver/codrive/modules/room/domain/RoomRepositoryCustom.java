package io.driver.codrive.modules.room.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.model.dto.RoomFilterDto;

@Repository
public interface RoomRepositoryCustom {
	List<Room> getRoomsByLanguageExcludingJoinedRoom(Long languageId, Long userId);

	Page<Room> filterRooms(RoomFilterDto request, Pageable pageable, SortType sortType);

}
