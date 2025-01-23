package io.driver.codrive.modules.room.event;

import java.util.List;

import io.driver.codrive.modules.user.domain.User;

public record RoomInactiveEvent(
	Long roomId,
	String roomTitle,
	List<User> members
) {
}
