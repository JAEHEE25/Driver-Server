package io.driver.codrive.modules.mappings.roomUserMapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomUserMappingRepository extends JpaRepository<RoomUserMapping, Long> {
}
