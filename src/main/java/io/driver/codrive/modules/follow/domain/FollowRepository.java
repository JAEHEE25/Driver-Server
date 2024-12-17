package io.driver.codrive.modules.follow.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	Optional<Follow> findByFollowingAndFollower(User following, User follower);

	boolean existsByFollowingAndFollower(User following, User follower);
}
