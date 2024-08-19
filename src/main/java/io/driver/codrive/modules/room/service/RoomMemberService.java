package io.driver.codrive.modules.room.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.model.SortType;
import io.driver.codrive.modules.room.model.response.RoomMembersResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
	private static final int NUMBER_OF_ROOM_MEMBERS = 10;
	private final RoomService roomService;
	private final UserService userService;
	private final RoomUserMappingService roomUserMappingService;

	@Transactional
	public RoomMembersResponse getRoomMembers(Long roomId, SortType sortType, int page) {
		Room room = roomService.getRoomById(roomId);
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		if (!room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹의 정보만 조회할 수 있습니다.");
		}

		Sort sort = SortType.getMemberSort(sortType);
		Pageable pageable = PageRequest.of(page, NUMBER_OF_ROOM_MEMBERS, sort);
		Page<User> members = roomUserMappingService.getRoomMembers(room, pageable);
		return RoomMembersResponse.of(members.getTotalPages(), members.toList());
	}

	/*

		Page<Room> rooms = roomRepository.findAll(pageable);
		return RoomListResponse.of(rooms.getTotalPages(), rooms.toList());
	 */

	@Transactional
	public void kickMember(Long roomId, Long userId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		User user = userService.getUserById(userId);
		roomUserMappingService.deleteRoomUserMapping(room, user);
	}

}
