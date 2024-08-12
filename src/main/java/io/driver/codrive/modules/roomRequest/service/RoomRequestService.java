package io.driver.codrive.modules.roomRequest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.driver.codrive.modules.roomRequest.model.request.PasswordRequest;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.roomRequest.domain.RoomRequestRepository;
import io.driver.codrive.modules.roomRequest.model.response.RoomRequestListResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomRequestService {
	private final UserService userService;
	private final RoomService roomService;
	private final RoomUserMappingService roomUserMappingService;
	private final RoomRequestRepository roomRequestRepository;

	@Transactional
	public void joinPrivateRoom(Long roomId, PasswordRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room room = roomService.getRoomById(roomId);
		String password = room.getPassword();
		if (password == null) {
			throw new IllegalArgumentApplicationException("해당 그룹은 공개 그룹입니다.");
		}

		if (!password.equals(request.password())) {
			throw new IllegalArgumentApplicationException("비밀번호가 일치하지 않습니다.");
		}

		if (roomUserMappingService.getRoomUserMapping(room, user) != null) {
			throw new IllegalArgumentApplicationException("이미 참여 중인 그룹입니다.");
		}

		roomUserMappingService.createRoomUserMapping(room, user);
	}

	@Transactional
	public void joinPublicRoom(Long roomId) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room room = roomService.getRoomById(roomId);
		if (room.getPassword() != null) {
			throw new IllegalArgumentApplicationException("해당 그룹은 비밀 그룹입니다.");
		}

		if (getRoomRequestByRoomAndUser(room, user) != null) {
			throw new IllegalArgumentApplicationException("이미 참여 요청한 그룹입니다.");
		}

		if (roomUserMappingService.getRoomUserMapping(room, user) != null) {
			throw new IllegalArgumentApplicationException("이미 참여 중인 그룹입니다.");
		}

		RoomRequest roomRequest = RoomRequest.toEntity(room, user);
		roomRequestRepository.save(roomRequest);
	}

	@Transactional
	public RoomRequest getRoomRequestById(Long roomRequestId) {
		return roomRequestRepository.findById(roomRequestId).orElseThrow(() -> new NotFoundApplcationException("참여 요청 데이터"));
	}

	@Transactional
	public RoomRequest getRoomRequestByRoomAndUser(Room room, User user) {
		return roomRequestRepository.findByRoomAndUser(room, user).orElse(null);
	}

	@Transactional
	public RoomRequestListResponse getRoomRequests(Long roomId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		return RoomRequestListResponse.of(roomRequestRepository.findAllByRoom(room));
	}

	@Transactional
	public void approveRequest(Long roomId, Long roomRequestId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		RoomRequest request = getRoomRequestById(roomRequestId);
		roomUserMappingService.createRoomUserMapping(room, request.getUser());
		roomRequestRepository.delete(request);
	}

	@Transactional
	public void denyRequest(Long roomId, Long roomRequestId) {
		Room room = roomService.getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		RoomRequest request = getRoomRequestById(roomRequestId);
		roomRequestRepository.delete(request);
	}

}
