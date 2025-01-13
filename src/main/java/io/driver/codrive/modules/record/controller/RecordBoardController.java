package io.driver.codrive.modules.record.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.auth.AuthenticatedUserId;
import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.global.model.SortType;
import io.driver.codrive.modules.record.model.response.BoardResponse;
import io.driver.codrive.modules.record.model.response.RecordMonthListResponse;
import io.driver.codrive.modules.record.model.response.UnsolvedMonthResponse;
import io.driver.codrive.modules.record.service.CountBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Record Board API", description = "문제 풀이 통계 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/records")
@RequiredArgsConstructor
public class RecordBoardController {
	private final CountBoardService countBoardService;

	@Operation(
		summary = "월별 문제 풀이 목록 조회",
		description = "선택한 달의 1일 ~ 말일 사이의 문제 풀이 목록을 조회합니다.",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지의 데이터 크기"),
			@Parameter(name = "sortType", in = ParameterIn.PATH, description = "페이지 정렬 기준 (NEW, OLD)"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordMonthListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다. || 지원하지 않는 정렬 방식입니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/month/{sortType}")
	public ResponseEntity<BaseResponse<RecordMonthListResponse>> getRecordsByMonth(@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "sortType") SortType sortType,
		@RequestParam(name = "pivotDate", required = false) String pivotDate,
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "size", defaultValue = "7") Integer size,
		@AuthenticatedUserId Long currentUserId) {
		RecordMonthListResponse response = countBoardService.getRecordsByMonth(userId, sortType, pivotDate,
			page, size, currentUserId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "월별 문제 풀이 현황 조회",
		description = "월별 문제 풀이 개수, 최장 문제 풀이 기간, 최대 문제 풀이 개수, 각 날짜의 문제 풀이 여부를 조회합니다.",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BoardResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/board")
	public ResponseEntity<BaseResponse<BoardResponse>> getRecordsBoard(@PathVariable(name = "userId") Long userId,
		@RequestParam(name = "pivotDate", required = false) String pivotDate) {
		BoardResponse response = countBoardService.getRecordsBoard(userId, pivotDate);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "문제 풀이 데이터가 없는 달 조회 ",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UnsolvedMonthResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/unsolved-months")
	public ResponseEntity<BaseResponse<UnsolvedMonthResponse>> getUnsolvedMonths(@PathVariable(name = "userId") Long userId,
		@RequestParam(name = "pivotDate", required = false) String pivotDate) {
		UnsolvedMonthResponse response = countBoardService.getUnsolvedMonths(userId, pivotDate);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
