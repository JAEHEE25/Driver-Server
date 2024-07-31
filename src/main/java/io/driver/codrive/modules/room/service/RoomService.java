package io.driver.codrive.modules.room.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.global.util.RoleUtils;
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
	private final ImageService imageService;
	private final RoomLanguageMappingService roomLanguageMappingService;
	private final RoomUserMappingService roomUserMappingService;
	private final RoomRepository roomRepository;

	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request, MultipartFile imageFile) throws IOException {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		String imageSrc = imageService.uploadImage(imageFile);
		Room savedRoom = roomRepository.save(request.toEntity(user, imageSrc));

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
	public RoomModifyResponse modifyRoom(Long roomId, RoomModifyRequest request, MultipartFile imageFile) throws IOException {
		Room room = getRoomById(roomId);
		String imageUrl = room.getImageSrc();
		String newImageUrl = imageService.modifyImage(imageUrl, imageFile);
		updateRoom(room, request, newImageUrl);
		return RoomModifyResponse.of(room);
	}

	@Transactional
	public void updateRoom(Room room, RoomModifyRequest request, String newImageUrl) {
		Room newRoom = request.toEntity(newImageUrl);
		room.changeTitle(newRoom.getTitle());
		room.changePassword(newRoom.getPassword());
		room.changeImageSrc(newRoom.getImageSrc());
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
