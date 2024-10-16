package io.driver.codrive.modules.notification.model.dto;

import io.driver.codrive.modules.notification.domain.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowEventDto extends NotificationEventDto {
	private Long followerId;

	public FollowEventDto(Notification notification, Long followerId) {
		super(notification);
		this.followerId = followerId;
	}
}
