package io.driver.codrive.modules.room.event;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;

public record RoomRequestedEvent(
	Room room,
	User applicant
) {
}
