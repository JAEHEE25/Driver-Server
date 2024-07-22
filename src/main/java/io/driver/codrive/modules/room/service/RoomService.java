package io.driver.codrive.modules.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.global.exception.NotFoundApplcationException;
import io.driver.codrive.modules.global.util.AuthUtils;
import io.driver.codrive.modules.mappings.roomLanguageMapping.service.RoomLanguageMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.model.RoomCreateRequest;
import io.driver.codrive.modules.room.model.RoomCreateResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final UserService userService;
	private final RoomLanguageMappingService roomLanguageMappingService;
	private final RoomRepository roomRepository;

	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request) {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		Room savedRoom = roomRepository.save(request.toEntity(user));
		roomLanguageMappingService.createRoomLanguageMapping(request.languages(), savedRoom);
		return RoomCreateResponse.of(savedRoom.getRoomId());
	}

	public Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundApplcationException("그룹"));
	}
}
