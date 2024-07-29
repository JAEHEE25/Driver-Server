package io.driver.codrive.modules.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.room.model.*;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.room.model.RoomListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@PostMapping
	public ResponseEntity<BaseResponse<RoomCreateResponse>> createRoom(@Valid @RequestBody RoomCreateRequest request) {
		RoomCreateResponse response = roomService.createRoom(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<BaseResponse<RoomDetailResponse>> getRoomDetail(@PathVariable(name = "roomId") Long roomId) {
		RoomDetailResponse response = roomService.getRoomDetail(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@PatchMapping("/{roomId}")
	public ResponseEntity<BaseResponse<RoomModifyResponse>> modifyRoom(@PathVariable(name = "roomId") Long roomId,
		@Valid @RequestBody RoomModifyRequest request) {
		RoomModifyResponse response = roomService.modifyRoom(roomId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@GetMapping("/{roomId}/members")
	public ResponseEntity<BaseResponse<RoomMembersResponse>> getRoomMembers(
		@PathVariable(name = "roomId") Long roomId) {
		RoomMembersResponse response = roomService.getRoomMembers(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@DeleteMapping("/{roomId}/kick/{userId}")
	public ResponseEntity<BaseResponse<Void>> kickMemeber(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "userId") Long userId) {
		roomService.kickMember(roomId, userId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@GetMapping
	public ResponseEntity<BaseResponse<RoomListResponse>> getRoomList(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "9") int size) {
		RoomListResponse response = roomService.getRoomList(page, size);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@GetMapping("{userId}/recommend")
	public ResponseEntity<BaseResponse<RoomRecommendResponse>> getRecommendRoomList(
		@PathVariable(name = "userId") Long userId) {
		RoomRecommendResponse response = roomService.getRecommendRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
