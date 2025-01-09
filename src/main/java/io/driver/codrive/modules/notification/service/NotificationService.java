package io.driver.codrive.modules.notification.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.domain.NotificationRepository;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.model.dto.NotificationEventDto;
import io.driver.codrive.modules.notification.model.request.NotificationReadRequest;
import io.driver.codrive.modules.notification.model.response.NotificationListResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final UserService userService;
	private final Map<Long, Sinks.Many<ServerSentEvent<NotificationEventDto>>> userNotificationSinks = new ConcurrentHashMap<>();

	@Transactional
	public Flux<ServerSentEvent<NotificationEventDto>> registerUser(Long userId) {
		User user = userService.getUserById(userId);
		Notification notification = Notification.create(user, null, NotificationType.CONNECT_START,
			String.valueOf(userId));
		return userNotificationSinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer())
			.asFlux()
			.doOnSubscribe(subscription -> {
				userNotificationSinks.get(userId).tryEmitNext(createServerSentEvent(notification));
				log.info("User [{}]의 알림 스트림을 시작합니다.", userId);
			});
	}

	@Async
	public void sendNotification(User user, Long dataId, NotificationType type, String... args) {
		Notification notification = createNotification(user, dataId, type, args);
		Long userId = user.getUserId();
		if (userNotificationSinks.containsKey(userId)) {
			userNotificationSinks.get(userId).tryEmitNext(createServerSentEvent(notification));
		} else {
			log.warn("User [{}]의 알림 스트림이 존재하지 않습니다.", userId);
		}
	}

	public void sendFollowNotification(User user, Long dataId, NotificationType type, String... args) {
		if (!notificationRepository.existsByUserAndDataIdAndNotificationType(user, dataId, type)) {
			sendNotification(user, dataId, type, args);
		}
	}


	protected Notification createNotification(User user, Long dataId, NotificationType type, String... args) {
		Notification notification = Notification.create(user, dataId, type, args);
		return notificationRepository.save(notification);
	}

	private ServerSentEvent<NotificationEventDto> createServerSentEvent(Notification notification) {
		return ServerSentEvent.<NotificationEventDto>builder()
			.event("message")
			.data(new NotificationEventDto(notification))
			.id(String.valueOf(notification.getNotificationId()))
			.comment(notification.getContent())
			.build();
	}

	public void unregisterUser(Long userId) {
		if (userNotificationSinks.containsKey(userId)) {
			userNotificationSinks.get(userId).tryEmitComplete();
			userNotificationSinks.remove(userId);
			log.info("User [{}]의 알림 스트림이 종료되었습니다.", userId);
		} else {
			throw new InternalServerErrorApplicationException("알림 스트림이 존재하지 않습니다.");
		}
	}

	public NotificationListResponse getNotifications(User user) {
		List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
		return NotificationListResponse.of(notifications);
	}

	@Transactional
	public void readNotification(NotificationReadRequest request) {
		List<Long> notificationIds = request.notificationIds();
		notificationIds.forEach(id ->
			notificationRepository.findById(id)
				.orElseThrow(() -> new NotFoundApplicationException("알림이 존재하지 않습니다."))
				.changeIsRead(true));
	}
}
