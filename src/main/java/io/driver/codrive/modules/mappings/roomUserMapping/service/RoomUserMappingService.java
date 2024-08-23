package io.driver.codrive.modules.mappings.roomUserMapping.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.model.SortType;
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
		if (room.hasMember(user)) {
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

	public Page<User> getRoomMembers(Room room, Pageable pageable) {
		return roomUserMappingRepository.findAllByRoom(room, pageable).map(RoomUserMapping::getUser);
	}

	public Page<Room> getJoinedRoomsByPage(Long userId, RoomStatus roomStatus, SortType sortType, Pageable pageable) {
		return roomUserMappingRepository.getRoomsByUserAndRoomStatusByPage(userId, roomStatus, sortType, pageable);
	}

	public List<Room> getJoinedRooms(User user) {
		return roomUserMappingRepository.findAllByUser(user).stream().map(RoomUserMapping::getRoom).toList();
	}

	public List<LanguageMemberCountDto> getLanguageMemberCountResponse(Room room) {
		return roomUserMappingRepository.getLanguageMemberCount(room);
	}
}
