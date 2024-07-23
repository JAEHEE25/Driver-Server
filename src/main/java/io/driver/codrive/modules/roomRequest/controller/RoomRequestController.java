package io.driver.codrive.modules.roomRequest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.roomRequest.model.PasswordRequest;
import io.driver.codrive.modules.roomRequest.model.RoomRequestListResponse;
import io.driver.codrive.modules.roomRequest.service.RoomRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/rooms")
@RequiredArgsConstructor
public class RoomRequestController {
	private final RoomRequestService roomRequestService;

	@PostMapping("/{roomId}/private")
	public ResponseEntity<BaseResponse<Void>> joinPrivateRoom(@PathVariable(name = "roomId") Long roomId,
		@Valid @RequestBody PasswordRequest request) {
		roomRequestService.joinPrivateRoom(roomId, request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@PostMapping("/{roomId}/public")
	public ResponseEntity<BaseResponse<Void>> joinSecretRoom(@PathVariable(name = "roomId") Long roomId) {
		roomRequestService.joinPublicRoom(roomId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@GetMapping("/{roomId}/requests")
	public ResponseEntity<BaseResponse<RoomRequestListResponse>> getRoomRequests(
		@PathVariable(name = "roomId") Long roomId) {
		RoomRequestListResponse response = roomRequestService.getRoomRequests(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@DeleteMapping("/{roomId}/approve/{requestId}")
	public ResponseEntity<BaseResponse<Void>> approveRequest(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "requestId") Long roomRequestId) {
		roomRequestService.approveRequest(roomId, roomRequestId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@DeleteMapping("/{roomId}/deny/{requestId}")
	public ResponseEntity<BaseResponse<Void>> denyRequest(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "requestId") Long roomRequestId) {
		roomRequestService.denyRequest(roomId, roomRequestId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
