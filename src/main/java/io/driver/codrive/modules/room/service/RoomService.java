package io.driver.codrive.modules.room.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.global.util.RoleUtils;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.roomLanguageMapping.service.RoomLanguageMappingService;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.model.*;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final UserService userService;
	private final RoomLanguageMappingService roomLanguageMappingService;
	private final RoomUserMappingService roomUserMappingService;
	private final RoomRepository roomRepository;

	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room savedRoom = roomRepository.save(request.toEntity(user));

		roomLanguageMappingService.createRoomLanguageMapping(request.tags(), savedRoom);
		roomUserMappingService.createRoomUserMapping(savedRoom, user);
		userService.changeUserRole(user, Role.OWNER);
		return RoomCreateResponse.of(savedRoom);
	}

	@Transactional
	public Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundApplcationException("그룹"));
	}

	@Transactional
	public RoomDetailResponse getRoomDetail(Long roomId) {
		Room room = getRoomById(roomId);
		return RoomDetailResponse.of(room);
	}

	@Transactional
	public RoomModifyResponse modifyRoom(Long roomId, RoomModifyRequest request) {
		Room room = getRoomById(roomId);
		updateRoom(room, request);
		return RoomModifyResponse.of(room);
	}

	@Transactional
	public void updateRoom(Room room, RoomModifyRequest request) {
		Room newRoom = request.toEntity();
		room.changeTitle(newRoom.getTitle());
		room.changePassword(newRoom.getPassword());
		room.changeImageSrc(newRoom.getImageSrc()); //todo 이미지 삭제 후 업로드
		room.changeCapacity(newRoom.getCapacity());
		room.changeIntroduce(newRoom.getIntroduce());
		room.changeInformation(newRoom.getInformation());
		updateLanguages(room, request.tags());
	}

	@Transactional
	public void updateLanguages(Room room, List<String> tags) {
		if (room.getLanguages() != tags) {
			roomLanguageMappingService.deleteRoomLanguageMapping(room.getRoomLanguageMappings(), room);
			roomLanguageMappingService.createRoomLanguageMapping(tags, room);
		}
	}

	@Transactional
	public RoomMembersResponse getRoomMembers(Long roomId) {
		Room room = getRoomById(roomId);
		List<User> members = room.getRoomMembers();
		return RoomMembersResponse.of(members);
	}

	@Transactional
	public void kickMember(Long roomId, Long userId) {
		Room room = getRoomById(roomId);
		User user = userService.getUserById(userId);
		RoleUtils.checkOwnedRoom(room, user);
		roomUserMappingService.deleteRoomUserMapping(room, user);
	}

	@Transactional
	public RoomListResponse getRoomList(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		List<RoomDetailResponse> rooms = roomRepository.findAll(pageable).map(RoomDetailResponse::of).toList();
		return RoomListResponse.of(rooms);
	}

	@Transactional
	public RoomRecommendResponse getRecommendRoomList(Long userId) {
		User user = userService.getUserById(userId);
		Language userLanguage = user.getLanguage();
		List<Room> rooms = userLanguage.getRoomsByLanguage();
		return RoomRecommendResponse.of(RoomDetailResponse.of(rooms));
	}
}
