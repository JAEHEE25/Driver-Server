package io.driver.codrive.modules.follow.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	Optional<Follow> findByFollowingAndFollower(User following, User follower);

	boolean existsByFollowingAndFollower(User following, User follower);

	@Query(value = """
		SELECT COUNT(*)
		FROM follow f
		WHERE f.following_id = :targetId
		  AND f.follower_id = :currentUserId
		  AND f.canceled = true
		""",
		nativeQuery = true)
	Long getCanceledFollowCount(@Param("targetId") Long targetId, @Param("currentUserId") Long currentUserId);
}
