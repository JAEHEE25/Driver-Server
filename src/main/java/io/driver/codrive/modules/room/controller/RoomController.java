package io.driver.codrive.modules.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.room.model.RoomCreateRequest;
import io.driver.codrive.modules.room.model.RoomCreateResponse;
import io.driver.codrive.modules.room.service.RoomService;
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
}
