package io.driver.codrive.modules.notification.model.request;

import java.util.List;

public record NotificationReadRequest(
	List<Long> notificationIds
) {
}
