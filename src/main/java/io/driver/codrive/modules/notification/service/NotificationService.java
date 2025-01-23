package io.driver.codrive.modules.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import io.driver.codrive.global.exception.NotFoundApplicationException;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.domain.NotificationRepository;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.model.dto.NotificationEventDto;
import io.driver.codrive.modules.notification.model.request.NotificationReadRequest;
import io.driver.codrive.modules.notification.model.response.NotificationListResponse;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private static final Long DEFAULT_TIMEOUT = 120L * 1000 * 60;
	private static final String DEFAULT_MESSAGE = "message";
	private final NotificationRepository notificationRepository;
	private final Map<Long, SseEmitter> userNotificationEmitters = new ConcurrentHashMap<>();

	@Transactional
	public SseEmitter registerUser(User user) {
		Long userId = user.getUserId();
		Notification notification = Notification.create(user, null, NotificationType.CONNECT_START,
			String.valueOf(userId));

		SseEmitter emitter = createSseEmitter(userId);
		sendNotification(userId, notification);
		log.info("User [{}]의 알림 스트림을 시작합니다.", userId);
		return emitter;
	}

	public void sendNotification(Long userId, Notification notification) {
		SseEmitter emitter = userNotificationEmitters.get(userId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event()
					.name(DEFAULT_MESSAGE)
					.id(String.valueOf(notification.getNotificationId()))
					.data(new NotificationEventDto(notification))
					.comment(notification.getContent()));
			} catch (IOException e) {
				log.warn("User [{}]에게 알림을 전송하지 못했습니다. 연결을 종료합니다.", userId);
				emitter.complete();
			}
		} else {
			log.warn("User [{}]의 알림 스트림이 존재하지 않습니다.", userId);
		}
	}

	private SseEmitter createSseEmitter(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		userNotificationEmitters.put(userId, emitter);

		emitter.onCompletion(() -> {
			log.info("User [{}]의 알림 스트림이 종료되었습니다.", userId);
			userNotificationEmitters.remove(userId);
		});

		emitter.onTimeout(() -> {
			log.info("User [{}]의 알림 스트림이 타임아웃되었습니다.", userId);
			emitter.complete();
		});

		return emitter;
	}

	public void unregisterUser(Long userId) {
		if (userNotificationEmitters.containsKey(userId)) {
			userNotificationEmitters.get(userId).complete();
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
