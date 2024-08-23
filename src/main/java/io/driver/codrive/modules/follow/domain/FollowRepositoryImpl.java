package io.driver.codrive.modules.follow.domain;

import static io.driver.codrive.modules.follow.domain.QFollow.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.model.SortType;

@Repository
public class FollowRepositoryImpl extends QuerydslRepositorySupport implements FollowRepositoryCustom {
	public FollowRepositoryImpl() {
		super(Follow.class);
	}

	@Override
	public List<Follow> getFollowings(Long userId, SortType sortType) {
		return from(follow)
			.where(follow.follower.userId.eq(userId))
			.orderBy(createFollowOrderSpecifier(sortType))
			.fetch();
	}

	private OrderSpecifier createFollowOrderSpecifier(SortType sortType) {
		if (sortType == SortType.NEW) {
			return new OrderSpecifier<>(Order.DESC, follow.createdAt);
		} else if (sortType == SortType.DICT) {
			return new OrderSpecifier<>(Order.ASC, follow.following.nickname);
		} else {
			throw new IllegalArgumentApplicationException("지원하지 않는 정렬 방식입니다.");
		}
	}

}
