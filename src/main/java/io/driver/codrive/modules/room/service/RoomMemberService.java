package io.driver.codrive.modules.room.service;


import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.model.response.RoomMembersResponse;
import io.driver.codrive.modules.room.model.response.RoomParticipantListResponse;
import io.driver.codrive.modules.room.model.response.RoomRankResponse;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.driver.codrive.modules.roomRequest.domain.UserRequestStatus;
import io.driver.codrive.modules.roomRequest.service.RoomRequestService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomMemberService {
	private static final int ROOM_MEMBERS_SIZE = 10;
	private final RoomService roomService;
	private final UserService userService;
	private final RoomRequestService roomRequestService;
	private final RoomUserMappingService roomUserMappingService;

	@Transactional(readOnly = true)
	public RoomMembersResponse getRoomMembers(Long userId, Long roomId, SortType sortType, int page) {
		Room room = roomService.getRoomById(roomId);
		User user = userService.getUserById(userId);
		if (!room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹의 정보만 조회할 수 있습니다.");
		}

		List<User> members = roomUserMappingService.getRoomMembers(room, sortType);
		Pageable pageable = PageRequest.of(page, ROOM_MEMBERS_SIZE);
		Page<User> membersByPage = PageUtils.getPage(members, pageable, members.size());
		return RoomMembersResponse.of(membersByPage.getTotalPages(), membersByPage.getContent());
	}

	@Transactional
	@PreAuthorize("@roomAccessHandler.isOwner(#roomId)")
	public RoomParticipantListResponse getRoomParticipants(Long roomId, SortType sortType, int page) {
		Room room = roomService.getRoomById(roomId);
		if (room.isFull()) {
			roomRequestService.changeWaitingRoomRequestStatus(room, UserRequestStatus.REQUESTED, UserRequestStatus.WAITING);
		} else {
			roomRequestService.changeWaitingRoomRequestStatus(room, UserRequestStatus.WAITING, UserRequestStatus.REQUESTED);
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
	@PreAuthorize("@roomAccessHandler.isOwner(#roomId)")
	public void kickMember(Long roomId, Long userId) {
		Room room = roomService.getRoomById(roomId);
		User user = userService.getUserById(userId);
		roomUserMappingService.deleteRoomUserMapping(room, user);
		roomRequestService.deleteRoomRequest(room, user);
	}

	public RoomRankResponse getRoomRank(Long roomId) {
		Room room = roomService.getRoomById(roomId);
		List<User> users = roomUserMappingService.getRoomRank(room);
		return RoomRankResponse.of(users);
	}

}
