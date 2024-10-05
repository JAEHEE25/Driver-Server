package io.driver.codrive.modules.notification.domain;

import io.driver.codrive.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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

	public static Notification create(Long userId, String content) {
		return Notification.builder()
			.content(content)
			.userId(userId)
			.isRead(false)
			.build();
	}
}
