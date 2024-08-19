package io.driver.codrive.modules.roomRequest.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.room.domain.Room;
import io.driver.codrive.modules.user.domain.User;

@Repository

public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
	Optional<RoomRequest> findByRoomAndUser(Room room, User user);

	List<RoomRequest> findAllByRoom(Room room);

	List<RoomRequest> findAllByRoomAndUserRequestStatus(Room room, UserRequestStatus userRequestStatus);
}
