package io.driver.codrive.modules.notification.domain;

public interface NotificationRepositoryCustom {
    void deleteReadNotificationsOlderThanWeeks(int weeks);
    void deleteUnreadNotificationsOlderThanWeeks(int weeks);
}
