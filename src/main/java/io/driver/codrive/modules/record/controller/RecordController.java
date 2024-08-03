package io.driver.codrive.modules.record.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.model.*;
import io.driver.codrive.modules.record.service.RecordService;
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

@Tag(name = "Record API", description = "문제 풀이 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/records")
@RequiredArgsConstructor
public class RecordController {
	private final RecordService recordService;

	@Operation(
		summary = "문제 풀이 등록",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = RecordSaveRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordCreateResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = {
				@ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
			})),
		}
	)
	@PostMapping
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createSavedRecord(
		@Valid @RequestBody RecordSaveRequest request) {
		RecordCreateResponse response = recordService.createRecord(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "문제 풀이 상세 조회",
		parameters = {
			@Parameter(name = "recordId", in = ParameterIn.PATH, required = true, description = "문제 풀이 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordDetailResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"문제 풀이 데이터를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{recordId}")
	public ResponseEntity<BaseResponse<RecordDetailResponse>> getRecordDetail(
		@PathVariable(name = "recordId") Long recordId) {
		RecordDetailResponse response = recordService.getRecordDetail(recordId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "문제 풀이 임시 저장",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = RecordTempRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordCreateResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = {
				@ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
			})),
		}
	)
	@PostMapping("/temp")
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createTempRecord(
		@Valid @RequestBody RecordTempRequest request) {
		RecordCreateResponse response = recordService.createRecord(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "임시 저장 문제 풀이 목록 조회",
		parameters = {
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지의 데이터 크기"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/records/temp")
	public ResponseEntity<BaseResponse<RecordListResponse>> getTempRecords(
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "size", defaultValue = "1") Integer size) {
		RecordListResponse response = recordService.getTempRecords(page, size);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "날짜별 문제 풀이 목록 조회",
		description = """
			선택한 날의 문제 풀이 목록을 조회합니다.
			""",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordListResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/day")
	public ResponseEntity<BaseResponse<RecordListResponse>> getRecordsByDay(@PathVariable(name = "userId") Long userId,
		@RequestParam(name = "pivotDate", required = false) String pivotDate) {
		RecordListResponse response = recordService.getRecordsByDay(userId, pivotDate);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "월별 문제 풀이 목록 조회",
		description = """
			선택한 달의 1일 ~ 말일 사이의 문제 풀이 목록을 조회합니다.
			""",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지의 데이터 크기"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/month")
	public ResponseEntity<BaseResponse<RecordListResponse>> getRecordsByMonth(@PathVariable(name = "userId") Long userId,
		@RequestParam(name = "pivotDate", required = false) String pivotDate,
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "size", defaultValue = "7") Integer size) {
		RecordListResponse response = recordService.getRecordsByMonth(userId, pivotDate, page, size);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "주간/월간 문제 풀이 개수 조회",
		description = """
			주간 데이터를 요청할 경우, 선택한 주의 월요일 00:00:00 ~ 일요일 23:59:59 사이의 문제 풀이 개수를 조회합니다.
			   
			월간 데이터를 조회할 경우, 선택한 달의 1일 ~ 말일 사이의 문제 풀이 개수를 조회합니다.
			""",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
			@Parameter(name = "period", in = ParameterIn.PATH, required = true, description = "주간/월간"),
			@Parameter(name = "pivotDate", in = ParameterIn.QUERY, description = "yyyy-MM-dd 형식의 날짜로 입력해야 합니다. 입력하지 않을 경우 현재 날짜로 조회합니다."),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordCountBoardResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("/{userId}/{period}/count")
	public ResponseEntity<BaseResponse<RecordCountBoardResponse>> getRecordsCount(
		@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "period") Period period,
		@RequestParam(name = "pivotDate", required = false) String pivotDate) {
		RecordCountBoardResponse response = recordService.getRecordsCount(userId, period, pivotDate);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "문제 풀이 수정",
		parameters = {
			@Parameter(name = "recordId", in = ParameterIn.PATH, required = true, description = "문제 풀이 ID"),
		},
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = RecordModifyRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordModifyResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = {
				@ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
			})),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"문제 풀이 데이터를 찾을 수 없습니다.\"}"))),
		}
	)
	@PatchMapping("/{recordId}")
	public ResponseEntity<BaseResponse<RecordModifyResponse>> modifyRecord(
		@PathVariable(name = "recordId") Long recordId, @Valid @RequestBody RecordModifyRequest request) {
		RecordModifyResponse response = recordService.modifyRecord(recordId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "문제 풀이 삭제",
		parameters = {
			@Parameter(name = "recordId", in = ParameterIn.PATH, required = true, description = "문제 풀이 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"문제 풀이 데이터를 찾을 수 없습니다.\"}"))),
		}
	)
	@DeleteMapping("/{recordId}")
	public ResponseEntity<BaseResponse<Void>> deleteRecord(@PathVariable(name = "recordId") Long recordId) {
		recordService.deleteRecord(recordId);
		return ResponseEntity.ok(BaseResponse.of(null));
	}
}
