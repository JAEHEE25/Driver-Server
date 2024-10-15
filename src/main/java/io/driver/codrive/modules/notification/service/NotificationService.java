package io.driver.codrive.modules.notification.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.exception.NotFoundApplcationException;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.domain.NotificationRepository;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.model.request.NotificationReadRequest;
import io.driver.codrive.modules.notification.model.response.NotificationListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final Map<Long, Sinks.Many<ServerSentEvent<Notification>>> userNotificationSinks = new ConcurrentHashMap<>();

	public Flux<ServerSentEvent<Notification>> registerUser() {
		Long userId = AuthUtils.getCurrentUserId();
		Notification notification = Notification.create(userId, NotificationType.CONNECT_START, String.valueOf(userId));
		return userNotificationSinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer())
			.asFlux()
			.doOnSubscribe(subscription -> {
				userNotificationSinks.get(userId).tryEmitNext(createServerSentEvent(notification));
				log.info("User [{}]의 알림 스트림을 시작합니다.", userId);
			});
	}

	private Notification createNotification(Long userId, NotificationType type, String... args) {
		Notification notification = Notification.create(userId, type, args);
		return notificationRepository.save(notification);
	}

	@Async
	public void sendNotification(Long userId, NotificationType type, String... args) {
		Notification notification = createNotification(userId, type, args);
		if (userNotificationSinks.containsKey(userId)) {
			userNotificationSinks.get(userId).tryEmitNext(createServerSentEvent(notification));
		} else {
			log.warn("User [{}]의 알림 스트림이 존재하지 않습니다.", userId);
		}
	}

	private ServerSentEvent<Notification> createServerSentEvent(Notification notification) {
		return ServerSentEvent.<Notification>builder()
			.event("message")
			.data(notification)
			.id(String.valueOf(notification.getNotificationId()))
			.comment(notification.getContent())
			.build();
	}

	public void unregisterUser() {
		Long userId = AuthUtils.getCurrentUserId();
		userNotificationSinks.get(userId).tryEmitComplete();
        userNotificationSinks.remove(userId);
		log.info("User [{}]의 알림 스트림을 종료합니다.", userId);
    }

	public NotificationListResponse getNotifications() {
		Long userId = AuthUtils.getCurrentUserId();
		List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
		return NotificationListResponse.of(notifications);
	}

	@Transactional
	public void readNotification(NotificationReadRequest request) {
		List<Long> notificationIds = request.notificationIds();
		notificationIds.forEach(id ->
			notificationRepository.findById(id)
			.orElseThrow(() -> new NotFoundApplcationException("알림이 존재하지 않습니다."))
			.changeIsRead(true));
	}
}
