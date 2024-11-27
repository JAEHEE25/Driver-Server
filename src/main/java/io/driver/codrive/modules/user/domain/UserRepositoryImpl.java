package io.driver.codrive.modules.user.domain;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static io.driver.codrive.modules.user.domain.QUser.user;
import static io.driver.codrive.modules.follow.domain.QFollow.follow;

import com.querydsl.core.types.dsl.Expressions;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {
	private static final int RANDOM_USER_LIMIT = 6;
	public UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public List<User> getRandomUsersExcludingMeAndFollowings(Long userId) {
		return from(user)
			.leftJoin(follow).on(follow.following.userId.eq(user.userId).and(follow.follower.userId.eq(userId)))
			.where(follow.followId.isNull(), user.userId.ne(userId), user.language.languageId.ne(1L))
			.orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
			.limit(RANDOM_USER_LIMIT)
			.fetch();
	}
}
