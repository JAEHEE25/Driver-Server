package io.driver.codrive.modules.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByUsername(String username);

	Optional<User> findByNickname(String nickname);

	Boolean existsByNickname(String nickname);

	Boolean existsByUsername(String username);

	@Modifying
    @Query("UPDATE User u SET u.successRate = 0")
    void resetSuccessRate();
}
