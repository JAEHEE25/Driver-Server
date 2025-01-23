package io.driver.codrive.modules.notification.event;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import io.driver.codrive.global.util.MessageUtils;
import io.driver.codrive.modules.follow.event.FollowerEvent;
import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.notification.domain.NotificationRepository;
import io.driver.codrive.modules.notification.domain.NotificationType;
import io.driver.codrive.modules.notification.service.NotificationService;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.room.event.PrivateRoomJoinedEvent;
import io.driver.codrive.modules.room.event.PublicRoomApprovedEvent;
import io.driver.codrive.modules.room.event.RoomInactiveEvent;
import io.driver.codrive.modules.room.event.RoomRequestedEvent;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	private final NotificationService notificationService;
	private final NotificationRepository notificationRepository;

	@TransactionalEventListener
	@Async
	public void sendRoomRequestedNotification(RoomRequestedEvent event) {
		sendNotificationToRoomOwner(event);
		sendNotificationToRoomApplicant(event);
	}

	@TransactionalEventListener
	@Async
	public void sendFollowerNotification(FollowerEvent event) {
		User target = event.target();
		User currentUser = event.currentUser();
		NotificationType type = NotificationType.FOLLOW;
		if (!notificationRepository.existsByUserAndDataIdAndNotificationType(target, currentUser.getUserId(), type)) {
			saveAndSendNotification(target, currentUser.getUserId(), type, currentUser.getNickname());
		}
	}

	@TransactionalEventListener
	@Async
	public void sendPublicRoomApprovedNotification(PublicRoomApprovedEvent event) {
		NotificationType type = NotificationType.PUBLIC_ROOM_APPROVE;
		Room room = event.room();
		String formattedTitle = MessageUtils.changeNameFormat(room.getTitle(), type.getLength());
		saveAndSendNotification(event.applicant(), room.getRoomId(), type, formattedTitle);
	}

	@TransactionalEventListener
	@Async
	public void sendPrivateRoomJoinedNotification(PrivateRoomJoinedEvent event) {
		NotificationType type = NotificationType.CREATED_PRIVATE_ROOM_JOIN;
		Room room = event.room();
		String formattedTitle = MessageUtils.changeNameFormat(room.getTitle(), type.getLength());
		String formattedNickname = MessageUtils.changeNameFormat(event.user().getNickname(), type.getLength());
		saveAndSendNotification(room.getOwner(), room.getRoomId(), type, formattedTitle, formattedNickname);
	}

	@TransactionalEventListener
	@Async
	public void sendRoomInactiveNotification(RoomInactiveEvent event) {
		NotificationType type = NotificationType.ROOM_STATUS_INACTIVE;
		String formattedTitle = MessageUtils.changeNameFormat(event.roomTitle(), type.getLength());
		event.members().forEach(member -> saveAndSendNotification(member, event.roomId(), type, formattedTitle));
	}

	private void saveAndSendNotification(User user, Long dataId, NotificationType type, String... args) {
		Notification notification = createNotification(user, dataId, type, args);
		notificationService.sendNotification(user.getUserId(), notification);
	}

	private void sendNotificationToRoomOwner(RoomRequestedEvent event) {
		NotificationType type = NotificationType.CREATED_PUBLIC_ROOM_REQUEST;
		Room room = event.room();
		String formattedTitle = MessageUtils.changeNameFormat(room.getTitle(), type.getLength());
		saveAndSendNotification(room.getOwner(), room.getRoomId(), type, formattedTitle);
	}

	private void sendNotificationToRoomApplicant(RoomRequestedEvent event) {
		NotificationType type = NotificationType.PUBLIC_ROOM_REQUEST;
		Room room = event.room();
		String formattedTitle = MessageUtils.changeNameFormat(room.getTitle(), type.getLength());
		saveAndSendNotification(event.applicant(), room.getRoomId(), type, formattedTitle);
	}

	private Notification createNotification(User user, Long dataId, NotificationType type, String... args) {
		Notification notification = Notification.create(user, dataId, type, args);
		return notificationRepository.save(notification);
	}
}
