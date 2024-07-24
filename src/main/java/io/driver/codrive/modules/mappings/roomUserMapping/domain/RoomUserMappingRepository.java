package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RoomUserMappingRepository extends JpaRepository<RoomUserMapping, Long> {
	Optional<RoomUserMapping> findByRoomAndUser(Room room, User user);
}
