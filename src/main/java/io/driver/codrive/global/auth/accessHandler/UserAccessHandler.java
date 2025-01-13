package io.driver.codrive.global.auth.accessHandler;

import org.springframework.stereotype.Component;

@Component
public class UserAccessHandler implements EntityAccessHandler {
	@Override
	public boolean isOwner(Long userId) {
		return isSameWithCurrentUserId(userId);
	}
}
