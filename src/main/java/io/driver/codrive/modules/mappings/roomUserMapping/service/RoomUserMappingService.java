package io.driver.codrive.modules.mappings.roomUserMapping.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.IllegalArgumentApplicationException;
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
		if (getRoomUserMapping(room, user) != null) {
			throw new IllegalArgumentApplicationException("이미 참여한 그룹입니다.");
		}

		RoomUserMapping mapping = roomUserMappingRepository.save(RoomUserMapping.toEntity(room, user));
		room.addRoomUserMappings(mapping);
		user.addRoomUserMappings(mapping);
	}

	@Transactional
	public RoomUserMapping getRoomUserMapping(Room room, User user) {
		return roomUserMappingRepository.findByRoomAndUser(room, user).orElse(null);
	}

	@Transactional
	public void deleteRoomUserMapping(Room room, User user) {
		RoomUserMapping mapping = getRoomUserMapping(room, user);
		room.deleteMember(mapping);
		user.deleteJoinedRoom(mapping);
		roomUserMappingRepository.delete(mapping);
	}
}
