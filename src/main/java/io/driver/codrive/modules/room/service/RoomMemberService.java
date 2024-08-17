package io.driver.codrive.modules.room.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.model.response.RoomMembersResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
	private final RoomService roomService;
	private final UserService userService;
	private final RoomUserMappingService roomUserMappingService;

	@Transactional
	public RoomMembersResponse getRoomMembers(Long roomId) {
		Room room = roomService.getRoomById(roomId);
		List<User> members = room.getRoomMembers();
		return RoomMembersResponse.of(members);
	}

	@Transactional
	public void kickMember(Long roomId, Long userId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		User user = userService.getUserById(userId);
		roomUserMappingService.deleteRoomUserMapping(room, user);
	}

}
