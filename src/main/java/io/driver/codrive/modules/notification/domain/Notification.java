package io.driver.codrive.modules.notification.domain;

import io.driver.codrive.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	private String content;

	private Long userId;

	private Boolean isRead;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Override
	public Long getOwnerId() {
		return userId;
	}

	public void changeIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public static Notification create(Long userId, NotificationType notificationType, String arg) {
		return Notification.builder()
			.content(notificationType.formatMessage(arg))
			.userId(userId)
			.isRead(false)
			.notificationType(notificationType)
			.build();
	}
}
