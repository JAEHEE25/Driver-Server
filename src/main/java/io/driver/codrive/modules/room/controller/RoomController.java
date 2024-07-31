package io.driver.codrive.modules.room.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.room.model.*;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.room.model.RoomListResponse;
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

@Tag(name = "Room API", description = "그룹 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@Operation(
		summary = "그룹 생성",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomCreateResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 언어입니다. || 잘못된 요청입니다. (metadata 제공)\"}"))),
		}
	)
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<RoomCreateResponse>> createRoom(
		@Valid @RequestPart(value = "request") RoomCreateRequest request,
		@RequestPart(value = "imageFile") MultipartFile imageFile) throws
		IOException {
		RoomCreateResponse response = roomService.createRoom(request, imageFile);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 상세 조회",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "그룹 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomDetailResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{roomId}")
	public ResponseEntity<BaseResponse<RoomDetailResponse>> getRoomDetail(@PathVariable(name = "roomId") Long roomId) {
		RoomDetailResponse response = roomService.getRoomDetail(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 목록 조회",
		parameters = {
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지 크기"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomListResponse.class))),
		}
	)
	@GetMapping
	public ResponseEntity<BaseResponse<RoomListResponse>> getRoomList(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "9") int size) {
		RoomListResponse response = roomService.getRoomList(page, size);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 수정",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "그룹 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomModifyResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping(value = "/{roomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<RoomModifyResponse>> modifyRoom(@PathVariable(name = "roomId") Long roomId,
		@Valid @RequestPart(value = "request") RoomModifyRequest request,
		@RequestPart(value = "imageFile") MultipartFile imageFile) throws IOException {
		RoomModifyResponse response = roomService.modifyRoom(roomId, request, imageFile);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 멤버 목록 조회",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomMembersResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{roomId}/members")
	public ResponseEntity<BaseResponse<RoomMembersResponse>> getRoomMembers(
		@PathVariable(name = "roomId") Long roomId) {
		RoomMembersResponse response = roomService.getRoomMembers(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 멤버 추방",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(value = "{\"code\": 403, \"message\": \"해당 그룹에 대한 권한이 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다. || 사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@DeleteMapping("/{roomId}/kick/{userId}")
	public ResponseEntity<BaseResponse<Void>> kickMemeber(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "userId") Long userId) {
		roomService.kickMember(roomId, userId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "오늘의 추천 그룹 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomRecommendResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("{userId}/recommend")
	public ResponseEntity<BaseResponse<RoomRecommendResponse>> getRecommendRoomList(
		@PathVariable(name = "userId") Long userId) {
		RoomRecommendResponse response = roomService.getRecommendRoomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
