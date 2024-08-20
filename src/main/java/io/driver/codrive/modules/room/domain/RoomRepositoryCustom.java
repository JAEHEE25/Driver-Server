package io.driver.codrive.modules.room.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepositoryCustom {
	List<Room> getRoomsByLanguageExcludingOwnRoom(Long languageId, Long userId);
}
