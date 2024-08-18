package io.driver.codrive.modules.room.domain;

import java.util.List;

public interface RoomRepositoryCustom {
	List<Room> getRoomsByLanguageExcludingOwnRoom(Long languageId, Long userId);
}
