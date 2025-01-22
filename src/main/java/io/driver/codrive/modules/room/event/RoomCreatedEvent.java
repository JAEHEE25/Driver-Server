package io.driver.codrive.modules.room.event;

public record RoomCreatedEvent(
	String nickname,
	String roomTitle
) {
}
