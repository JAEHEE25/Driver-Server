package io.driver.codrive.modules.mappings.roomUserMapping.domain;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.mappings.roomUserMapping.model.LanguageMemberCountDto;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomStatus;

@Repository
public interface RoomUserMappingRepositoryCustom {
	Page<Room> getRoomsByUserAndRoomStatus(Long userId, RoomStatus roomStatus, Pageable pageable);
	List<LanguageMemberCountDto> getLanguageMemberCount(Room room);
}
