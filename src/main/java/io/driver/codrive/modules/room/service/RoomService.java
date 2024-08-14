package io.driver.codrive.modules.room.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.mappings.roomLanguageMapping.service.RoomLanguageMappingService;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.model.SortType;
import io.driver.codrive.modules.room.model.request.RoomCreateRequest;
import io.driver.codrive.modules.room.model.request.RoomModifyRequest;
import io.driver.codrive.modules.room.model.response.*;
import io.driver.codrive.modules.user.domain.Role;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private static final int NUMBER_OF_ROOMS = 9;
	private static final int NUMBER_OF_RANDOM_ROOMS = 6;
	private final UserService userService;
	private final ImageService imageService;
	private final RoomLanguageMappingService roomLanguageMappingService;
	private final RoomUserMappingService roomUserMappingService;
	private final RoomRepository roomRepository;

	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request, MultipartFile imageFile) throws IOException {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		String imageSrc = imageService.uploadImage(imageFile);
		Room savedRoom = roomRepository.save(request.toRoom(user, imageSrc));

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
	public RoomUuidResponse getRoomInfoByUuid(String uuid) {
		Room room = roomRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundApplcationException("그룹"));
		return RoomUuidResponse.of(room);
	}

	@Transactional
	public RoomModifyResponse modifyRoom(Long roomId, RoomModifyRequest request, MultipartFile imageFile) throws IOException {
		Room room = getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		String imageUrl = room.getImageSrc();
		String newImageUrl = imageService.modifyImage(imageUrl, imageFile);
		updateRoom(room, request, newImageUrl);
		return RoomModifyResponse.of(room);
	}

	@Transactional
	public void updateRoom(Room room, RoomModifyRequest request, String newImageUrl) {
		Room newRoom = request.toRoom(newImageUrl);
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
	public RoomListResponse getRooms(SortType sortType, int page) {
		Sort sort = SortType.getSort(sortType);
		Pageable pageable = PageRequest.of(page, NUMBER_OF_ROOMS, sort);
		Page<Room> rooms = roomRepository.findAll(pageable);
		return RoomListResponse.of(rooms.toList(), rooms.getTotalPages());
	}

	@Transactional
	public RoomRecommendResponse getRecommendRoomRandomList(Long userId) {
		User user = userService.getUserById(userId);
		Language userLanguage = user.getLanguage();
		List<Room> rooms = userLanguage.getRoomsByLanguage();
		Collections.shuffle(rooms);
		List<Room> randomRooms = rooms.stream().limit(NUMBER_OF_RANDOM_ROOMS).toList();
		return RoomRecommendResponse.of(RoomDetailResponse.of(randomRooms));
	}

	@Transactional
	public void kickMember(Long roomId, Long userId) {
		Room room = getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		User user = userService.getUserById(userId);
		roomUserMappingService.deleteRoomUserMapping(room, user);
	}

	@Transactional
	public RoomListResponse searchRooms(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Room> rooms = roomRepository.findByTitleContaining(keyword, pageable);
		return RoomListResponse.of(rooms.toList(), rooms.getTotalPages());
	}

}
