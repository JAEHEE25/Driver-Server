package io.driver.codrive.modules.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.notification.model.request.NotificationReadRequest;
import io.driver.codrive.modules.notification.model.response.NotificationListResponse;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;
	@Operation(
		summary = "알림 스트림 등록"
	)
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter registerUser() {
		return notificationService.registerUser();
	}

	@Operation(
		summary = "알림 스트림 해제"
	)
	@DeleteMapping
	public void unregisterUser() {
		notificationService.unregisterUser();
	}

	@Operation(
		summary = "알림 목록 조회",
		description = "사용자의 알림 목록을 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NotificationListResponse.class))),
		}
	)
	@GetMapping("/list")
	public ResponseEntity<BaseResponse<NotificationListResponse>> getNotifications() {
		NotificationListResponse response = notificationService.getNotifications();
		return ResponseEntity.ok(BaseResponse.of(response));
	}

	@Operation(
		summary = "알림 읽음 처리",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = NotificationReadRequest.class)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
		}
	)
	@PostMapping("/read")
	public ResponseEntity<BaseResponse<Void>> readNotification(@RequestBody NotificationReadRequest request) {
		notificationService.readNotification(request);
		return ResponseEntity.ok(BaseResponse.of(null));
	}
}
