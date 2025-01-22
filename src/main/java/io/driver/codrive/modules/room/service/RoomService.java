package io.driver.codrive.modules.room.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.global.util.MessageUtils;
import io.driver.codrive.global.util.PageUtils;
import io.driver.codrive.modules.language.domain.Language;
import io.driver.codrive.modules.language.service.LanguageService;
import io.driver.codrive.modules.mappings.roomLanguageMapping.service.RoomLanguageMappingService;
import io.driver.codrive.modules.mappings.roomUserMapping.service.RoomUserMappingService;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.domain.RoomRepository;
import io.driver.codrive.modules.room.domain.RoomStatus;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.event.RoomCreatedEvent;
import io.driver.codrive.modules.room.model.dto.RoomFilterDto;
import io.driver.codrive.modules.room.model.request.RoomCreateRequest;
import io.driver.codrive.modules.room.model.request.RoomFilterRequest;
import io.driver.codrive.modules.room.model.request.RoomModifyRequest;
import io.driver.codrive.modules.room.model.response.*;
import io.driver.codrive.modules.roomRequest.domain.UserRequestStatus;
import io.driver.codrive.modules.roomRequest.service.RoomRequestService;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.room.model.response.CreatedRoomListResponse;
import io.driver.codrive.modules.room.model.response.JoinedRoomListResponse;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private static final String BASE_ROOM_IMAGE_URL = "https://codrive-image.s3.ap-northeast-2.amazonaws.com/images/default/codrive-room-default-image.jpg";
	private static final int ROOMS_SIZE = 9;
	private final UserService userService;
	private final ImageService imageService;
	private final RoomLanguageMappingService roomLanguageMappingService;
	private final RoomUserMappingService roomUserMappingService;
	private final LanguageService languageService;
	private final RoomRepository roomRepository;
	private final NotificationService notificationService;
	private final RoomRequestService roomRequestService;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public RoomCreateResponse createRoom(Long userId, RoomCreateRequest request, MultipartFile imageFile) throws IOException {
		User user = userService.getUserById(userId);
		String imageSrc = BASE_ROOM_IMAGE_URL;
		if (imageFile != null) {
			imageSrc = imageService.uploadImage(imageFile);
		}
		Room savedRoom = roomRepository.save(request.toRoom(user, imageSrc));
		roomLanguageMappingService.createRoomLanguageMapping(request.tags(), savedRoom);
		roomUserMappingService.createRoomUserMapping(savedRoom, user);

		eventPublisher.publishEvent(new RoomCreatedEvent(user.getNickname(), savedRoom.getTitle()));
		return RoomCreateResponse.of(savedRoom);
	}

	public Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundApplicationException("그룹"));
	}

	public Long getOwnerIdByRoomId(Long roomId) {
		return roomRepository.findOwnerIdByRoomId(roomId);
	}

	@Transactional(readOnly = true)
	public RoomDetailResponse getRoomDetail(User user, Long roomId) {
		Room room = getRoomById(roomId);
		int requestedCount = roomRequestService.getRoomRequestByRoom(room).size();
		return RoomDetailResponse.of(room, requestedCount, room.hasMember(user));
	}

	@Transactional(readOnly = true)
	public JoinedRoomInfoResponse getJoinedRoomInfo(Long userId, Long roomId) {
		User user = userService.getUserById(userId);
		Room room = getRoomById(roomId);
		if (!room.hasMember(user)) {
			throw new IllegalArgumentApplicationException("활동 중인 그룹의 정보만 조회할 수 있습니다.");
		}
		String password = null;
		if (room.getOwner().equals(user)) {
			password = room.getPassword();
		}
		int approvedCount = roomRequestService.getRoomRequestCountByRoomAndRequestStatus(room, UserRequestStatus.JOINED);
		int requestedCount = roomRequestService.getRoomRequestByRoom(room).size() - approvedCount;
		return JoinedRoomInfoResponse.of(room, password, approvedCount, requestedCount,
			roomUserMappingService.getLanguageMemberCountResponse(room));
	}

	public RoomUuidResponse getRoomInfoByUuid(String uuid) {
		Room room = roomRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundApplicationException("그룹"));
		return RoomUuidResponse.of(room);
	}

	@Transactional
	@PreAuthorize("@roomAccessHandler.isOwner(#roomId)")
	public RoomModifyResponse modifyRoom(Long roomId, RoomModifyRequest request, MultipartFile imageFile) throws IOException {
		Room room = getRoomById(roomId);
		String imageSrc = room.getImageSrc();
		if (imageFile != null) {
			imageSrc = imageService.modifyImage(imageSrc, imageFile);
		}
		updateRoom(room, request, imageSrc);
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
	@PreAuthorize("@roomAccessHandler.isOwner(#roomId)")
	public void changeRoomStatus(Long roomId, String status) {
		Room room = getRoomById(roomId);
		RoomStatus roomStatus = RoomStatus.getRoomStatusByName(status);
		room.changeRoomStatus(roomStatus);

		if (roomStatus == RoomStatus.INACTIVE) {
			room.getMembers().forEach(member -> notificationService.saveAndSendNotification(member, roomId,
				NotificationType.ROOM_STATUS_INACTIVE, MessageUtils.changeNameFormat(room.getTitle(),
					NotificationType.ROOM_STATUS_INACTIVE.getLength())));
		}
	}

	@Transactional(readOnly = true)
	public JoinedRoomTitleResponse getJoinedRoomTitle(Long userId) {
		User user = userService.getUserById(userId);
		List<Room> rooms = user.getJoinedRooms();
		return JoinedRoomTitleResponse.of(rooms);
	}

	@Transactional(readOnly = true)
	public JoinedRoomListResponse getJoinedRoomList(Long userId, User currentUser, SortType sortType, Integer page, String status) {
		User user = userService.getUserById(userId);
		RoomStatus roomStatus = getRoomStatus(status);
		if (page != null) {
			Pageable pageable = PageRequest.of(page, ROOMS_SIZE);
			Page<Room> rooms = roomUserMappingService.getJoinedRoomsByPage(user.getUserId(), roomStatus, sortType,
				pageable);
			return JoinedRoomListResponse.of(rooms.getTotalPages(), rooms.getContent(), currentUser);
		}
		return JoinedRoomListResponse.of(getJoinedRoomsBySort(user, sortType), currentUser);
	}

	private List<Room> getJoinedRoomsBySort(User user, SortType sortType) {
		List<Room> rooms;
		rooms = user.getJoinedRooms()
			.stream()
			.filter(room -> (room.getRoomStatus() == RoomStatus.ACTIVE))
			.toList();
		return rooms.stream().sorted(SortType.getJoinedRoomComparator(sortType)).collect(Collectors.toList());
	}

	@Transactional
	public CreatedRoomListResponse getCreatedRoomList(Long userId, SortType sortType, int page, String status) {
		Sort sort = SortType.getRoomSort(sortType);
		PageUtils.validatePageable(page, ROOMS_SIZE);
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE, sort);
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

	@Transactional(readOnly = true)
	public RoomRecommendResponse getRecommendRoomRandomList(Long userId) {
		User user = userService.getUserById(userId);
		List<Room> rooms = roomRepository.getRoomsByLanguageExcludingJoinedRoom(user.getLanguage().getLanguageId(),
			user.getUserId());
		return RoomRecommendResponse.of(rooms);
	}

	@Transactional(readOnly = true)
	public RoomListResponse searchRooms(Long userId, String keyword, int page) {
		PageUtils.validatePageable(page, ROOMS_SIZE);
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE);
		Page<Room> rooms = roomRepository.findByTitleContaining(keyword, pageable);
		User user = userService.getUserById(userId);
		return RoomListResponse.of(rooms.getTotalPages(), rooms.getContent(), user);
	}

	@Transactional(readOnly = true)
	public RoomListResponse filterRooms(Long userId, SortType sortType, RoomFilterRequest request, int page) {
		Pageable pageable = PageRequest.of(page, ROOMS_SIZE);
		RoomFilterDto roomFilterDto = getRoomFilterDto(request);
		Page<Room> rooms = roomRepository.filterRooms(roomFilterDto, pageable, sortType);
		User user = userService.getUserById(userId);
		return RoomListResponse.of(rooms.getTotalPages(), rooms.getContent(), user);
	}

	private RoomFilterDto getRoomFilterDto(RoomFilterRequest request) {
		List<Long> tagIds;
		if (request.tags() == null || request.tags().isEmpty()) {
			tagIds = List.of();
		} else {
			tagIds = request.tags().stream().map(tag -> {
				Language language = languageService.getLanguageByName(tag);
				return language.getLanguageId();
			}).toList();
		}
		return RoomFilterDto.toRoomFilterDto(request, tagIds);
	}

	@Transactional(readOnly = true)
	public RecentRoomResponse getRecentRooms(Long userId) {
		User user = userService.getUserById(userId);
		List<Room> rooms = user.getJoinedRooms();
		if (!rooms.isEmpty()) {
			rooms = rooms.stream().sorted(getRecentRoomComparator()).collect(Collectors.toList());
		}
		return RecentRoomResponse.of(rooms, user);
	}

	private Comparator<Room> getRecentRoomComparator() {
		return Comparator.comparing(Room::getLastUpdatedAt).reversed();
	}

}
