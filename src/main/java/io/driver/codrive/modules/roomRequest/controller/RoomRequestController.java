package io.driver.codrive.modules.roomRequest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.roomRequest.model.PasswordRequest;
import io.driver.codrive.modules.roomRequest.model.RoomRequestListResponse;
import io.driver.codrive.modules.roomRequest.service.RoomRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Room Request API", description = "그룹 참여 요청 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/rooms")
@RequiredArgsConstructor
public class RoomRequestController {
	private final RoomRequestService roomRequestService;

	@Operation(
		summary = "비밀 그룹 참여",
		description = "비밀번호를 입력하여 일치할 경우 해당 그룹에 참여합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
		},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = PasswordRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "400", content = @Content(examples =
				@ExampleObject(value = "{\"code\": 400, \"message\": \"해당 그룹은 공개 그룹입니다.|| 비밀번호가 일치하지 않습니다. || 이미 참여 중인 그룹입니다. || 잘못된 요청입니다. (error field 제공)\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@PostMapping("/{roomId}/private")
	public ResponseEntity<BaseResponse<Void>> joinPrivateRoom(@PathVariable(name = "roomId") Long roomId,
		@Valid @RequestBody PasswordRequest request) {
		roomRequestService.joinPrivateRoom(roomId, request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "공개 그룹 참여",
		description = "해당 그룹에 참여 요청을 보냅니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "400", content = @Content(examples =
				@ExampleObject(value = "{\"code\": 400, \"message\": \"해당 그룹은 비밀 그룹입니다.|| 이미 참여 중인 그룹입니다. || 이미 참여 요청한 그룹입니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@PostMapping("/{roomId}/public")
	public ResponseEntity<BaseResponse<Void>> joinPublicRoom(@PathVariable(name = "roomId") Long roomId) {
		roomRequestService.joinPublicRoom(roomId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "그룹 참여 요청 목록 조회",
		description = "그룹장만 가능합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomRequestListResponse.class))),
			@ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(value = "{\"code\": 403, \"message\": \"해당 그룹에 대한 권한이 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{roomId}/requests")
	public ResponseEntity<BaseResponse<RoomRequestListResponse>> getRoomRequests(
		@PathVariable(name = "roomId") Long roomId) {
		RoomRequestListResponse response = roomRequestService.getRoomRequests(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 참여 요청 승인",
		description = "그룹장만 가능합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "requestId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(value = "{\"code\": 403, \"message\": \"해당 그룹에 대한 권한이 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다. || 참여 요청 데이터를 찾을 수 없습니다. \"}"))),
		}
	)
	@DeleteMapping("/{roomId}/approve/{requestId}")
	public ResponseEntity<BaseResponse<Void>> approveRequest(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "requestId") Long roomRequestId) {
		roomRequestService.approveRequest(roomId, roomRequestId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "그룹 참여 요청 거절",
		description = "그룹장만 가능합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "requestId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(value = "{\"code\": 403, \"message\": \"해당 그룹에 대한 권한이 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다. || 참여 요청 데이터를 찾을 수 없습니다. \"}"))),
		}
	)
	@DeleteMapping("/{roomId}/deny/{requestId}")
	public ResponseEntity<BaseResponse<Void>> denyRequest(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "requestId") Long roomRequestId) {
		roomRequestService.denyRequest(roomId, roomRequestId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
