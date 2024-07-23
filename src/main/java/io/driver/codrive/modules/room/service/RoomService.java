package io.driver.codrive.modules.room.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.roomLanguageMapping.domain.RoomLanguageMapping;
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

		List<RoomLanguageMapping> mappings = roomLanguageMappingService.getRoomLanguageMappingsByRequest(
			request.languages(), savedRoom);
		roomLanguageMappingService.createRoomLanguageMapping(mappings, savedRoom);

		roomUserMappingService.createRoomUserMapping(savedRoom, user);
		userService.changeUserRole(user, Role.OWNER);
		return RoomCreateResponse.of(savedRoom);
	}

	@Transactional
	public RoomDetailResponse getRoomDetail(Long roomId) {
		Room room = getRoomById(roomId);
		return RoomDetailResponse.of(room);
	}

	@Transactional
	public Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundApplcationException("그룹"));
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
		room.changeImageUrl(newRoom.getImageUrl()); //todo 이미지 삭제 후 업로드
		room.changeCapacity(newRoom.getCapacity());
		room.changeIntroduction(newRoom.getIntroduction());
		room.changeInformation(newRoom.getInformation());
		updateLanguages(room, request.languages());
	}

	@Transactional
	public void updateLanguages(Room room, List<String> newLanguages) {
		if (room.getLanguages() != newLanguages) {
			roomLanguageMappingService.deleteRoomLanguageMapping(room.getRoomLanguageMappings());
			List<RoomLanguageMapping> newMappings = roomLanguageMappingService.getRoomLanguageMappingsByRequest(
				newLanguages, room);
			roomLanguageMappingService.createRoomLanguageMapping(newMappings, room);
		}
	}
}
