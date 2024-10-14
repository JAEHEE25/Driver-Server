package io.driver.codrive.modules.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.model.BaseResponse;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.model.response.NotificationListResponse;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequestMapping(APIConstants.API_PREFIX + "/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;
	@Operation(
		summary = "알림 스트림 등록",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
		}
	)
	@GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Notification>> registerUser() {
		return notificationService.registerUser();
	}

	@Operation(
		summary = "알림 스트림 해제",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\"}"))),
		}
	)
	@DeleteMapping
	public void unregisterUser() {
		notificationService.unregisterUser();
	}

	@Operation(
		summary = "알림 목록 조회",
		description = "사용자의 알림 목록을 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"SUCCESS\", \"data\": {\"notifications\": [{\"notificationId\": 1, \"content\": \"알림 내용\"}]}}"))),
		}
	)
	@GetMapping
	public ResponseEntity<BaseResponse<NotificationListResponse>> getNotifications() {
		NotificationListResponse response = notificationService.getNotifications();
		return ResponseEntity.ok(BaseResponse.of(response));
	}
}
