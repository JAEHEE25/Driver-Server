package io.driver.codrive.modules.notification.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.driver.codrive.modules.user.domain.User;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
	List<Notification> findByUserOrderByCreatedAtDesc(User user);
	List<Notification> findAllByDataId(Long dataId);
	List<Notification> findAllByDataIdIn(List<Long> dataIds);

	boolean existsByUserAndDataIdAndNotificationType(User user, Long dataId, NotificationType notificationType);
}
