package io.driver.codrive.modules.record.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.record.model.request.RecordModifyRequest;
import io.driver.codrive.modules.record.model.request.RecordSaveRequest;
import io.driver.codrive.modules.record.model.request.RecordTempRequest;
import io.driver.codrive.modules.record.model.response.*;
import io.driver.codrive.modules.record.service.RecordCreateService;
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
	private final RecordCreateService<RecordSaveRequest> recordSaveService;
	private final RecordCreateService<RecordTempRequest> recordTempService;

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
				@ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 플랫폼입니다. || 지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
			})),
		}
	)
	@PostMapping
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createSavedRecord(@Valid @RequestBody RecordSaveRequest request) {
		RecordCreateResponse response = recordSaveService.createRecord(request);
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
				@ExampleObject(value = "{\"code\": 400, \"message\": \"임시 저장 최대 개수를 초과했습니다. || "
					+ "지원하지 않는 플랫폼입니다. || 지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
			})),
		}
	)
	@PostMapping("/temp")
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createTempRecord(@Valid @RequestBody RecordTempRequest request) {
		RecordCreateResponse response = recordTempService.createRecord(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "임시 저장 문제 풀이 목록 조회",
		parameters = {
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 번호"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지의 데이터 크기"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TempRecordListResponse.class))),
			@ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"페이지 정보가 올바르지 않습니다.\"}"))),
		}
	)
	@GetMapping("/temp")
	public ResponseEntity<BaseResponse<TempRecordListResponse>> getTempRecords(
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "size", defaultValue = "1") Integer size) {
		TempRecordListResponse response = recordService.getTempRecordsByPage(page, size);
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
				@ExampleObject(value = "{\"code\": 400, \"message\": \"지원하지 않는 플랫폼입니다. || 지원하지 않는 문제 유형입니다. || 잘못된 요청입니다. (error field 제공)\"}"),
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

	@Operation(
		summary = "최근 문제 풀이 목록 조회",
		parameters = {
			@Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "사용자 ID"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RecordRecentListResponse.class))),
			@ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"사용자를 찾을 수 없습니다.\"}"))),
		}
	)
	@GetMapping("{userId}/recent")
	public ResponseEntity<BaseResponse<RecordRecentListResponse>> getRecentRecords(@PathVariable(name = "userId") Long userId) {
		RecordRecentListResponse response = recordService.getRecentRecords(userId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
