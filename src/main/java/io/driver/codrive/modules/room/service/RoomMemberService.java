package io.driver.codrive.modules.room.service;


import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.model.response.RoomMembersResponse;
import io.driver.codrive.modules.room.model.response.RoomParticipantListResponse;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.driver.codrive.modules.roomRequest.service.RoomRequestService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
	private static final int ROOM_MEMBERS_SIZE = 10;
	private final RoomService roomService;
	private final UserService userService;
	private final RoomRequestService roomRequestService;
	private final RoomUserMappingService roomUserMappingService;

	@Transactional
	public RoomMembersResponse getRoomMembers(Long roomId, SortType sortType, int page) {
		Room room = roomService.getRoomById(roomId);
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		if (!room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹의 정보만 조회할 수 있습니다.");
		}

		Sort sort = SortType.getMemberSort(sortType);
		Pageable pageable = PageRequest.of(page, ROOM_MEMBERS_SIZE, sort);
		PageUtils.validatePageable(pageable);
		Page<User> members = roomUserMappingService.getRoomMembers(room, pageable);
		return RoomMembersResponse.of(members.getTotalPages(), members.getContent());
	}

	@Transactional
	public RoomParticipantListResponse getRoomParticipants(Long roomId, SortType sortType, int page) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		if (!room.isFull()) {
			roomRequestService.changeWaitingRoomRequestToRequested(room);
		}
		Sort sort = SortType.getRoomRequestSort(sortType);
		Pageable pageable = PageRequest.of(page, ROOM_MEMBERS_SIZE, sort);
		return getRoomParticipantListResponse(room, pageable);
	}

	@Transactional
	public RoomParticipantListResponse getRoomParticipantListResponse(Room room, Pageable pageable) {
		Page<RoomRequest> requestsByPage = roomRequestService.getRoomParticipants(room, pageable);
		return RoomParticipantListResponse.of(requestsByPage.getTotalPages(), requestsByPage.getContent());
	}

	@Transactional
	public void kickMember(Long roomId, Long userId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		User user = userService.getUserById(userId);
		roomUserMappingService.deleteRoomUserMapping(room, user);
		roomRequestService.deleteRoomRequest(room, user);
	}

}
