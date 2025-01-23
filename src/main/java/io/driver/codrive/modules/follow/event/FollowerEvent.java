package io.driver.codrive.modules.follow.event;

import io.driver.codrive.modules.user.domain.User;

public record FollowerEvent (
	User target,
	User currentUser
) {
}
