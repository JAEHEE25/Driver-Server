package io.driver.codrive.modules.follow.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.driver.codrive.global.model.SortType;

@Repository
public interface FollowRepositoryCustom {
	List<Follow> getFollowings(Long userId, SortType sortType);
}
