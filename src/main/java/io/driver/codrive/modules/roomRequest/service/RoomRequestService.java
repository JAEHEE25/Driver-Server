package io.driver.codrive.modules.roomRequest.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.global.util.MessageUtils;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.domain.RoomStatus;
import io.driver.codrive.modules.roomRequest.domain.RoomRequest;
import io.driver.codrive.modules.roomRequest.domain.UserRequestStatus;
import io.driver.codrive.modules.roomRequest.model.request.PasswordRequest;
import io.driver.codrive.modules.roomRequest.domain.RoomRequestRepository;
import io.driver.codrive.modules.roomRequest.model.response.RoomRequestListResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomRequestService {
	private final UserService userService;
	private final RoomUserMappingService roomUserMappingService;
	private final NotificationService notificationService;
	private final RoomRepository roomRepository;
	private final RoomRequestRepository roomRequestRepository;

	@Transactional
	public void joinPrivateRoom(Long roomId, PasswordRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room room = getRoomById(roomId);
		checkRoomStatus(room);
		checkRoomMember(room, user);

		if (room.isFull()) {
			throw new IllegalArgumentApplicationException("정원이 초과되었습니다.");
		}

		if (room.isPublicRoom()) {
			throw new IllegalArgumentApplicationException("해당 그룹은 공개 그룹입니다.");
		}

		if (!room.isCorrectPassword(request.password())) {
			throw new IllegalArgumentApplicationException("비밀번호가 일치하지 않습니다.");
		}

		RoomRequest roomRequest = RoomRequest.toPrivateRoomRequest(room, user);
		saveRoomRequest(roomRequest, room);
		roomUserMappingService.createRoomUserMapping(room, user);
		notificationService.sendNotification(room.getOwner(), room.getRoomId(), NotificationType.CREATED_PRIVATE_ROOM_JOIN,
			MessageUtils.changeNameFormat(room.getTitle(), NotificationType.CREATED_PRIVATE_ROOM_JOIN.getLength()),
			MessageUtils.changeNameFormat(user.getNickname(), NotificationType.CREATED_PRIVATE_ROOM_JOIN.getLength()));
	}

	@Transactional
	public void joinPublicRoom(Long roomId) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room room = getRoomById(roomId);
		checkRoomStatus(room);
		checkRoomMember(room, user);

		if (!room.isPublicRoom()) {
			throw new IllegalArgumentApplicationException("해당 그룹은 비밀 그룹입니다.");
		}

		if (room.existUserRequest(user)) {
			throw new IllegalArgumentApplicationException("이미 참여 요청한 그룹입니다.");
		}

		RoomRequest roomRequest = RoomRequest.toPublicRoomRequest(room, user);
		if (room.isFull()) {
			roomRequest.changeRoomRequestStatus(UserRequestStatus.WAITING);
		}
		saveRoomRequest(roomRequest, room);

		notificationService.sendNotification(room.getOwner(), room.getRoomId(), NotificationType.CREATED_PUBLIC_ROOM_REQUEST,
			MessageUtils.changeNameFormat(room.getTitle(), NotificationType.CREATED_PUBLIC_ROOM_REQUEST.getLength()));
		notificationService.sendNotification(user, room.getRoomId(), NotificationType.PUBLIC_ROOM_REQUEST,
			MessageUtils.changeNameFormat(room.getTitle(), NotificationType.PUBLIC_ROOM_REQUEST.getLength()));
	}

	private void checkRoomMember(Room room, User user) {
		if (room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("이미 참여 중인 그룹입니다.");
		}
	}

	private void checkRoomStatus(Room room) {
		if (!room.compareStatus(RoomStatus.ACTIVE)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹이 아닙니다.");
		}
	}

	@Transactional
	protected void saveRoomRequest(RoomRequest roomRequest, Room room) {
		roomRequestRepository.save(roomRequest);
		room.addRoomRequests(roomRequest);
	}

	@Transactional
	public RoomRequest getRoomRequestById(Long roomRequestId) {
		return roomRequestRepository.findById(roomRequestId)
			.orElseThrow(() -> new NotFoundApplcationException("참여 요청 데이터"));
	}

	@Transactional
	public RoomRequestListResponse getRoomRequests(Long roomId) {
		Room room = getRoomById(roomId);
		int approvedCount = getRoomRequestCountByRoomAndRequestStatus(room, UserRequestStatus.JOINED);
		return RoomRequestListResponse.of(approvedCount, getRoomsRequestByRoom(room));
	}

	public Page<RoomRequest> getRoomParticipants(Room room, Pageable pageable) {
		return roomRequestRepository.findAllByRoom(room, pageable);
	}

	@Transactional
	public void approveRequest(Long roomId, Long roomRequestId) {
		Room room = getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		RoomRequest roomRequest = getRoomRequestById(roomRequestId);

		if (!roomRequest.compareStatus(UserRequestStatus.REQUESTED)) {
			throw new IllegalArgumentApplicationException("승인할 수 없는 요청입니다.");
		}

		if (room.isFull()) {
			throw new IllegalArgumentApplicationException("정원이 초과되었습니다.");
		}

		roomRequest.changeRoomRequestStatus(UserRequestStatus.JOINED);
		roomUserMappingService.createRoomUserMapping(room, roomRequest.getUser());

		notificationService.sendNotification(roomRequest.getUser(), room.getRoomId(),
			NotificationType.PUBLIC_ROOM_APPROVE, MessageUtils.changeNameFormat(room.getTitle(), NotificationType.PUBLIC_ROOM_APPROVE.getLength()));
	}

	public void changeWaitingRoomRequestStatus(Room room, UserRequestStatus originStatus, UserRequestStatus newStatus) {
		List<RoomRequest> roomRequests = roomRequestRepository.findAllByRoomAndUserRequestStatus(room, originStatus);
		roomRequests.forEach(roomRequest -> roomRequest.changeRoomRequestStatus(newStatus));
	}

	@Transactional
	public void deleteRoomRequest(Room room, User user) {
		RoomRequest roomRequest = roomRequestRepository.findByRoomAndUser(room, user).orElseThrow(() -> new NotFoundApplcationException("참여 요청 데이터"));
		roomRequestRepository.delete(roomRequest);
	}

	@Transactional(readOnly = true)
	public Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId)
			.orElseThrow(() -> new NotFoundApplcationException("그룹"));
	}

	@Transactional(readOnly = true)
	public int getRoomRequestCountByRoomAndRequestStatus(Room room, UserRequestStatus userRequestStatus) {
		return roomRequestRepository.findAllByRoomAndUserRequestStatus(room, userRequestStatus).size();
	}

	@Transactional(readOnly = true)
	public List<RoomRequest> getRoomsRequestByRoom(Room room) {
		return roomRequestRepository.findAllByRoom(room);
	}
}
