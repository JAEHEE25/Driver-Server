package io.driver.codrive.modules.room.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.room.model.request.RoomCreateRequest;
import io.driver.codrive.modules.room.model.request.RoomFilterRequest;
import io.driver.codrive.modules.room.model.request.RoomModifyRequest;
import io.driver.codrive.modules.room.model.response.*;
import io.driver.codrive.modules.room.service.RoomService;
import io.driver.codrive.modules.room.model.response.CreatedRoomListResponse;
import io.driver.codrive.modules.room.model.response.JoinedRoomListResponse;
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
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 언어입니다. || 잘못된 요청입니다. (error field 제공)\"}"))),
		}
	)
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<RoomCreateResponse>> createRoom(
		@Valid @RequestPart(value = "request") RoomCreateRequest request,
		@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
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
		summary = "활동 중인 그룹 정보 조회",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "그룹 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JoinedRoomInfoResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"활동 중인 그룹의 정보만 조회할 수 있습니다.\"}"))),
		}
	)
	@GetMapping("/{roomId}/join")
	public ResponseEntity<BaseResponse<JoinedRoomInfoResponse>> getJoinedRoomInfo(@PathVariable(name = "roomId") Long roomId) {
		JoinedRoomInfoResponse response = roomService.getJoinedRoomInfo(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "UUID로 그룹 정보 조회",
		parameters = {
			@Parameter(name = "uuid", in = ParameterIn.PATH, required = true, description = "그룹 UUID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomUuidResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/uuid/{uuid}")
	public ResponseEntity<BaseResponse<RoomUuidResponse>> getRoomInfoByUuid(@PathVariable(name = "uuid") String uuid) {
		RoomUuidResponse response = roomService.getRoomInfoByUuid(uuid);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "참여한 그룹 제목 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JoinedRoomTitleResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/member/title")
	public ResponseEntity<BaseResponse<JoinedRoomTitleResponse>> getJoinedRoomTitle(@PathVariable(name = "userId") Long userId) {
		JoinedRoomTitleResponse response = roomService.getJoinedRoomTitle(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "참여한 그룹 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "sortType", in = ParameterIn.PATH, required = true, description = "페이지 정렬 기준 (NEW, DICT)"),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "status", in = ParameterIn.QUERY, description = "그룹 상태 (선택하지 않을 경우 전체 데이터를 조회합니다.)", schema = @Schema(allowableValues = {"CLOSED", "ACTIVE", "INACTIVE"})),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JoinedRoomListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 상태 타입입니다. || 지원하지 않는 정렬 방식입니다. || 페이지 정보가 올바르지 않습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/member/{sortType}")
	public ResponseEntity<BaseResponse<JoinedRoomListResponse>> getJoinedRoomList(@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "sortType") SortType sortType, @RequestParam(name = "page", required = false) Integer page,
		@RequestParam(name = "status", required = false) String status) {
		JoinedRoomListResponse response = roomService.getJoinedRoomList(userId, sortType, page, status);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "생성한 그룹 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "sortType", in = ParameterIn.PATH, description = "페이지 정렬 기준 (NEW, DICT)"),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "status", in = ParameterIn.QUERY, description = "그룹 상태 (선택하지 않을 경우 전체 데이터를 조회합니다.)", schema = @Schema(allowableValues = {"CLOSED", "ACTIVE", "INACTIVE"})),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CreatedRoomListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 상태 타입입니다. || 지원하지 않는 정렬 방식입니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/owner/{sortType}")
	public ResponseEntity<BaseResponse<CreatedRoomListResponse>> getCreatedRoomList(@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "sortType") SortType sortType, @RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "status", required = false) String status) {
		CreatedRoomListResponse response = roomService.getCreatedRoomList(userId, sortType, page, status);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 수정",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "그룹 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomModifyResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 언어입니다."
				+ "|| 잘못된 요청입니다. (error field 제공) || 모집 인원은 현재 인원보다 적을 수 없습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping(value = "/{roomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<RoomModifyResponse>> modifyRoom(@PathVariable(name = "roomId") Long roomId,
		@Valid @RequestPart(value = "request") RoomModifyRequest request,
		@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
		RoomModifyResponse response = roomService.modifyRoom(roomId, request, imageFile);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 상태 변경",
		description = "그룹장만 가능합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "그룹 ID"),
			@Parameter(name = "status", in = ParameterIn.PATH, required = true, description = "그룹 상태", schema = @Schema(allowableValues = {"CLOSED", "ACTIVE", "INACTIVE"})),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 상태 타입입니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping("/{roomId}/status/{status}")
	public ResponseEntity<BaseResponse<Void>> changeRoomStatus(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "status") String status) {
		roomService.changeRoomStatus(roomId, status);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@Operation(
		summary = "추천 그룹 목록 조회",
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
		RoomRecommendResponse response = roomService.getRecommendRoomRandomList(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 검색",
		parameters = {
			@Parameter(name = "keyword", in = ParameterIn.QUERY, required = true, description = "검색 키워드"),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/search")
	public ResponseEntity<BaseResponse<RoomListResponse>> searchRooms(@RequestParam(name = "keyword") String keyword,
		@RequestParam(name = "page", defaultValue = "0") Integer page) {
		RoomListResponse response = roomService.searchRooms(keyword, page);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "전체 그룹 목록 필터링 조회",
		description = "'ALL' 태그를 선택했을 경우 모든 언어를 리스트에 담아서 요청해야 합니다.",
		parameters = {
			@Parameter(name = "sortType", in = ParameterIn.PATH, description = "페이지 정렬 기준 (NEW, DICT)"),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoomListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/filter/{sortType}")
	public ResponseEntity<BaseResponse<RoomListResponse>> filterRooms(@PathVariable(name = "sortType") SortType sortType,
		@Parameter RoomFilterRequest request,
		@RequestParam(name = "page", defaultValue = "0") Integer page) {
		RoomListResponse response = roomService.filterRooms(sortType, request, page);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "최근 활동 중인 그룹 목록 조회",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecentRoomResponse.class))),
		}
	)
	@GetMapping("/recent")
	public ResponseEntity<BaseResponse<RecentRoomResponse>> getRecentRooms() {
		RecentRoomResponse response = roomService.getRecentRooms();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

}
