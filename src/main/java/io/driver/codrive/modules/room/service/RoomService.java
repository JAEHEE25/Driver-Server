package io.driver.codrive.modules.room.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.mappings.roomLanguageMapping.service.RoomLanguageMappingService;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.domain.RoomStatus;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.model.request.RoomCreateRequest;
import io.driver.codrive.modules.room.model.request.RoomModifyRequest;
import io.driver.codrive.modules.room.model.response.*;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.room.model.response.CreatedRoomListResponse;
import io.driver.codrive.modules.room.model.response.JoinedRoomListResponse;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private static final int ROOMS_SIZE = 9;
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
	public JoinedRoomInfoResponse getJoinedRoomInfo(Long roomId) {
		Room room = getRoomById(roomId);
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		if (!room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹의 정보만 조회할 수 있습니다.");
		}
		return JoinedRoomInfoResponse.of(room, roomUserMappingService.getLanguageMemberCountResponse(room));
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
	public void changeRoomStatus(Long roomId, String status) {
		Room room = getRoomById(roomId);
		AuthUtils.checkOwnedEntity(room);
		RoomStatus roomStatus = RoomStatus.getRoomStatusByName(status);
		room.changeRoomStatus(roomStatus);
	}

	@Transactional
	public RoomListResponse getRooms(SortType sortType, int page) {
		Sort sort = SortType.getRoomSort(sortType);
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE, sort);
		PageUtils.validatePageable(pageable);
		Page<Room> rooms = roomRepository.findAll(pageable);
		return RoomListResponse.of(rooms.getTotalPages(), rooms.getContent());
	}

	@Transactional
	public JoinedRoomTitleResponse getJoinedRoomTitle(Long userId) {
		User user = userService.getUserById(userId);
		List<Room> rooms = user.getJoinedRooms();
		return JoinedRoomTitleResponse.of(rooms);
	}

	@Transactional
	public JoinedRoomListResponse getJoinedRoomList(Long userId, SortType sortType, Integer page, String status) {
		User user = userService.getUserById(userId);
		RoomStatus roomStatus = getRoomStatus(status);
		if (page != null) {
			Pageable pageable = PageRequest.of(page, ROOMS_SIZE);
			Page<Room> rooms = roomUserMappingService.getJoinedRoomsByPage(user.getUserId(), roomStatus, sortType, pageable);
			return JoinedRoomListResponse.of(rooms.getTotalPages(), rooms.getContent());
		}
		return JoinedRoomListResponse.of(getJoinedRoomsByStatusAndSort(roomStatus, user, sortType));
	}

	private List<Room> getJoinedRoomsByStatusAndSort(RoomStatus roomStatus, User user, SortType sortType) {
		List<Room> rooms;
		if (roomStatus == null) {
			rooms = user.getJoinedRooms();
		} else {
			rooms = user.getJoinedRooms().stream().filter(room -> room.getRoomStatus() == roomStatus).toList();
		}
		return rooms.stream().sorted(SortType.getJoinedRoomComparator(sortType)).collect(Collectors.toList());
	}

	@Transactional
	public CreatedRoomListResponse getCreatedRoomList(Long userId, SortType sortType, int page, String status) {
		Sort sort = SortType.getRoomSort(sortType);
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE, sort);
		PageUtils.validatePageable(pageable);
		User user = userService.getUserById(userId);
		Page<Room> rooms = getCreatedRoomsByRoomStatus(user, status, pageable);
		return CreatedRoomListResponse.of(rooms.getTotalPages(), rooms.getContent());
	}

	private RoomStatus getRoomStatus(String status) {
		RoomStatus roomStatus = null;
		if (status != null) {
			roomStatus = RoomStatus.getRoomStatusByName(status);
		}
		return roomStatus;
	}

	private Page<Room> getCreatedRoomsByRoomStatus(User user, String status, Pageable pageable) {
		RoomStatus roomStatus = getRoomStatus(status);
		if (roomStatus == null) {
			return roomRepository.findAllByOwner(user, pageable);
		} else {
			return roomRepository.findAllByOwnerAndRoomStatus(user, getRoomStatus(status), pageable);
		}
	}

	@Transactional
	public RoomRecommendResponse getRecommendRoomRandomList(Long userId) {
		User user = userService.getUserById(userId);
		AuthUtils.checkOwnedEntity(user);
		List<Room> rooms = roomRepository.getRoomsByLanguageExcludingOwnRoom(user.getLanguage().getLanguageId(), user.getUserId());
		return RoomRecommendResponse.of(rooms);
	}

	@Transactional
	public RoomListResponse searchRooms(String keyword, int page) {
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE);
		PageUtils.validatePageable(pageable);
		Page<Room> rooms = roomRepository.findByTitleContaining(keyword, pageable);
		return RoomListResponse.of(rooms.getTotalPages(), rooms.getContent());
	}

	@Transactional
	public RecentRoomResponse getRecentRooms() {
		User user = userService.getUserById(AuthUtils.getCurrentUserId());
		List<Room> rooms = user.getJoinedRooms();
		if (!rooms.isEmpty()) {
			rooms = rooms.stream().sorted(getRecentRoomComparator()).collect(Collectors.toList());
		}
		return RecentRoomResponse.of(rooms);
	}

	private Comparator<Room> getRecentRoomComparator() {
		return Comparator.comparing(Room::getLastUpdatedAt).reversed();
	}

}
