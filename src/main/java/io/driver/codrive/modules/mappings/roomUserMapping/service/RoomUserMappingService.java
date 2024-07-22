package io.driver.codrive.modules.mappings.roomUserMapping.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMappingRepository;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomUserMappingService {
	private final RoomUserMappingRepository roomUserMappingRepository;

	@Transactional
	public void createRoomUserMapping(Room room, User user) {
		RoomUserMapping mapping = roomUserMappingRepository.save(RoomUserMapping.toEntity(room, user));
		room.addRoomUserMappings(mapping);
		user.addRoomUserMappings(mapping);
	}
}
