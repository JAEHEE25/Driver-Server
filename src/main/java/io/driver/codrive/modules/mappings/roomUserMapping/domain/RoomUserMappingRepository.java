package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RoomUserMappingRepository extends JpaRepository<RoomUserMapping, Long>, RoomUserMappingRepositoryCustom {
	Optional<RoomUserMapping> findByRoomAndUser(Room room, User user);
	Page<RoomUserMapping> findAllByRoom(Room room, Pageable pageable);
	List<RoomUserMapping> findAllByRoom(Room room);
}
