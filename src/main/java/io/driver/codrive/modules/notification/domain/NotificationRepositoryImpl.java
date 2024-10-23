package io.driver.codrive.modules.notification.domain;

import static io.driver.codrive.modules.notification.domain.QNotification.*;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPADeleteClause;

public class NotificationRepositoryImpl extends QuerydslRepositorySupport implements NotificationRepositoryCustom {

	public NotificationRepositoryImpl() {
		super(Notification.class);
	}

	@Override
	public void deleteReadNotificationsOlderThanWeeks(int weeks) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(weeks);

        JPADeleteClause deleteClause = new JPADeleteClause(getEntityManager(), notification);
        deleteClause.where(notification.isRead.isTrue()
                           .and(notification.createdAt.before(cutoffDate)))
                    .execute();
	}

	@Override
	public void deleteUnreadNotificationsOlderThanWeeks(int weeks) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(weeks);

        JPADeleteClause deleteClause = new JPADeleteClause(getEntityManager(), notification);
        deleteClause.where(notification.isRead.isFalse()
                           .and(notification.createdAt.before(cutoffDate)))
                    .execute();
    }
}
