package io.driver.codrive.modules.global.util;

import io.driver.codrive.modules.global.exception.handler.ForbiddenApplcationException;
import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleUtils {

	public void checkOwnedRoom(Room room, User user) {
		if (!room.getOwner().equals(user)) {
			throw new ForbiddenApplcationException("해당 그룹");
		}
	}
}
