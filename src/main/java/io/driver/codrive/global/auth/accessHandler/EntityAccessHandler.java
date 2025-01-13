package io.driver.codrive.global.auth.accessHandler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import io.driver.codrive.global.util.AuthUtils;

@Component
public interface EntityAccessHandler {
	boolean isOwner(Long entityId);

	default boolean isSameWithCurrentUserId(Long ownerId) {
		Long currentUserId = AuthUtils.getCurrentUserId();
		return Objects.equals(currentUserId, ownerId);
	}
}
