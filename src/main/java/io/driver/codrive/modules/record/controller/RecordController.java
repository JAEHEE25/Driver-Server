package io.driver.codrive.modules.record.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import io.driver.codrive.modules.record.model.RecordCreateRequest;
import io.driver.codrive.modules.record.model.RecordCreateResponse;
import io.driver.codrive.modules.record.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/records")
@RequiredArgsConstructor
public class RecordController {
	private final RecordService recordService;
	@PostMapping
	public ResponseEntity<BaseResponse<RecordCreateResponse>> createRoom(@Valid @RequestBody RecordCreateRequest request) {
		RecordCreateResponse response = recordService.createRecord(request);
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
