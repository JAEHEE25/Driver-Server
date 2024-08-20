package io.driver.codrive.modules.user.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {
	List<User> getRandomUsersExceptMeAndFollowings(Long userId);
}
