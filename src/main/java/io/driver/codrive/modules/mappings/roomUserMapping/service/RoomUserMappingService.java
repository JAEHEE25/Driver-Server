package io.driver.codrive.modules.mappings.roomUserMapping.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMapping;
import io.driver.codrive.modules.mappings.roomUserMapping.domain.RoomUserMappingRepository;
import io.driver.codrive.modules.mappings.roomUserMapping.model.LanguageMemberCountDto;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomStatus;
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
		RoomUserMapping mapping = roomUserMappingRepository.save(RoomUserMapping.toRoomUserMapping(room, user));
		room.addRoomUserMappings(mapping);
		room.changeMemberCount(room.getMemberCount() + 1);
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
		room.changeMemberCount(room.getMemberCount() - 1);
		user.deleteJoinedRoom(mapping);
		roomUserMappingRepository.delete(mapping);
	}

	public Page<Room> getJoinedRooms(Long userId, RoomStatus roomStatus, Pageable pageable) {
		return roomUserMappingRepository.getRoomsByUserAndRoomStatus(userId, roomStatus, pageable);
	}

	public List<LanguageMemberCountDto> getLanguageMemberCountResponse(Room room) {
		return roomUserMappingRepository.getLanguageMemberCount(room);
	}
}
