package io.driver.codrive.global.auth.accessHandler;

import org.springframework.stereotype.Component;

import io.driver.codrive.modules.room.service.RoomService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomAccessHandler implements EntityAccessHandler {

	private final RoomService roomService;

	@Override
	public boolean isOwner(Long roomId) {
		Long ownerId = roomService.getOwnerIdByRoomId(roomId);
		return isSameWithCurrentUserId(ownerId);
	}
}