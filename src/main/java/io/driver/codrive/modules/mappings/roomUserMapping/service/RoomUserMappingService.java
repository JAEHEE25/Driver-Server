package io.driver.codrive.modules.mappings.roomUserMapping.service;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
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
		user.addRoomUserMappings(mapping);
	}

	@Transactional
	public RoomUserMapping getRoomUserMapping(Room room, User user) {
		return roomUserMappingRepository.findByRoomAndUser(room, user).orElse(null);
	}

	@Transactional
	public void deleteRoomUserMapping(Room room, User user) {
		RoomUserMapping mapping = getRoomUserMapping(room, user);
		if (mapping == null) {
			throw new NotFoundApplicationException("멤버");
		}
		room.deleteMember(mapping);
		user.deleteJoinedRoom(mapping);
		roomUserMappingRepository.delete(mapping);
	}

	public List<User> getRoomMembers(Room room, SortType sortType) {
		return roomUserMappingRepository.getRoomMembers(room, sortType);
	}

	public Page<Room> getJoinedRoomsByPage(Long userId, RoomStatus roomStatus, SortType sortType, Pageable pageable) {
		return roomUserMappingRepository.getRoomsByUserAndRoomStatusExcludingOwnByPage(userId, roomStatus, sortType, pageable);
	}

	public List<LanguageMemberCountDto> getLanguageMemberCountResponse(Room room) {
		return roomUserMappingRepository.getLanguageMemberCount(room);
	}

	public List<User> getRoomRank(Room room) {
		return roomUserMappingRepository.getRoomRank(room, LocalDate.now());
	}
}
