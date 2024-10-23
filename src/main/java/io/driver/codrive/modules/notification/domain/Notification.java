package io.driver.codrive.modules.notification.domain;

import io.driver.codrive.global.entity.BaseEntity;
import io.driver.codrive.modules.user.domain.User;
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

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	private Boolean isRead;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private Long dataId;

	@Override
	public Long getOwnerId() {
		return user.getUserId();
	}

	public void changeIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public static Notification create(User user, Long dataId, NotificationType type, String... args) {
		return Notification.builder()
			.content(type.formatMessage(args))
			.user(user)
			.isRead(false)
			.notificationType(type)
			.dataId(dataId)
			.build();
	}
}
