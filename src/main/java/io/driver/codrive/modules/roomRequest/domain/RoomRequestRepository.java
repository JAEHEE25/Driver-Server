package io.driver.codrive.modules.roomRequest.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
}
