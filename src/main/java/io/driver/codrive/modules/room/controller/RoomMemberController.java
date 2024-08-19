package io.driver.codrive.modules.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.room.model.response.RoomMembersResponse;
import io.driver.codrive.modules.room.service.RoomMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Room Member API", description = "그룹 멤버 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/rooms")
@RequiredArgsConstructor
public class RoomMemberController {
	private final RoomMemberService roomMemberService;

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
		RoomMembersResponse response = roomMemberService.getRoomMembers(roomId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "그룹 멤버 추방",
		description = "그룹장만 가능합니다.",
		parameters = {
			@Parameter(name = "roomId", in = ParameterIn.PATH, required = true),
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"그룹을 찾을 수 없습니다. || 사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@DeleteMapping("/{roomId}/kick/{userId}")
	public ResponseEntity<BaseResponse<Void>> kickMemeber(@PathVariable(name = "roomId") Long roomId,
		@PathVariable(name = "userId") Long userId) {
		roomMemberService.kickMember(roomId, userId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
