package io.driver.codrive.modules.record.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.record.model.*;
import io.driver.codrive.modules.record.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/records")
@RequiredArgsConstructor
public class RecordController {
	private final RecordService recordService;

	@PostMapping
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createRecord(
		@Valid @RequestBody RecordCreateRequest request) {
		RecordCreateResponse response = recordService.createRecord(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@PatchMapping("/{recordId}")
	public ResponseEntity<BaseResponse<RecordModifyResponse>> modifyRecord(
		@PathVariable(name = "recordId") Long recordId, @Valid @RequestBody RecordModifyRequest request) {
		RecordModifyResponse response = recordService.modifyRecord(recordId, request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@GetMapping("/{recordId}")
	public ResponseEntity<BaseResponse<RecordDetailResponse>> getRecordDetail(@PathVariable(name = "recordId") Long recordId) {
		RecordDetailResponse response = recordService.getRecordDetail(recordId);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
