package io.driver.codrive.modules.room.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
	Page<Room> findAll(Pageable pageable);
	Optional<Room> findByUuid(String uuid);
	Page<Room> findByTitleContaining(String keyword, Pageable pageable);
	Page<Room> findAllByOwner(User user, Pageable pageable);
}
