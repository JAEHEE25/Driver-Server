package io.driver.codrive.domain.userRoomMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoomMapRepository extends JpaRepository<UserRoomMap, Long> {
}
