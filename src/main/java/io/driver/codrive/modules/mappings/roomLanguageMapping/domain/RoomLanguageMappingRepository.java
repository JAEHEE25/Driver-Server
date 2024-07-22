package io.driver.codrive.modules.mappings.roomLanguageMapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomLanguageMappingRepository extends JpaRepository<RoomLanguageMapping, Long> {
}
