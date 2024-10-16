package io.driver.codrive.modules.notification.model.dto;

import io.driver.codrive.modules.notification.domain.Notification;
import io.driver.codrive.modules.room.domain.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomEventDto extends NotificationEventDto {
	private Long roomId;
	private Long ownerId;
	private String uuid;

	public RoomEventDto(Notification notification, Room room) {
		super(notification);
		this.roomId = room.getRoomId();
		this.ownerId = room.getOwnerId();
		this.uuid = room.getUuid();
	}
}
