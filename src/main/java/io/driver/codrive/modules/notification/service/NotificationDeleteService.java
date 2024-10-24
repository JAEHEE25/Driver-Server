package io.driver.codrive.modules.notification.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.domain.NotificationRepository;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDeleteService {
	private static final int READ_NOTIFICATIONS_CLEANUP_WEEKS = 4;
	private static final int UNREAD_NOTIFICATIONS_CLEANUP_WEEKS = 8;
	private final NotificationRepository notificationRepository;

	@Transactional
	public void deleteFollowNotifications(User user) {
		Long userId = user.getUserId();
		List<Notification> followNotifications = notificationRepository.findAllByDataId(userId);
		notificationRepository.deleteAll(followNotifications);
	}

	@Transactional
	public void deleteRoomNotifications(User user) {
		List<Room> createdRooms = user.getCreatedRooms();
		List<Notification> roomNotifications = notificationRepository.findAllByDataIdIn(
			createdRooms.stream().map(Room::getRoomId).toList());
		notificationRepository.deleteAll(roomNotifications);
	}

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 00:00에 실행
    @Transactional
    public void cleanUpOldNotifications() {
        notificationRepository.deleteReadNotificationsOlderThanWeeks(READ_NOTIFICATIONS_CLEANUP_WEEKS);
        notificationRepository.deleteUnreadNotificationsOlderThanWeeks(UNREAD_NOTIFICATIONS_CLEANUP_WEEKS);
		log.info("알림 데이터 정리가 완료되었습니다.");
    }
}
