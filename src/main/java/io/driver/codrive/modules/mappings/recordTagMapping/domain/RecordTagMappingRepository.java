package io.driver.codrive.modules.mappings.recordTagMapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordTagMappingRepository extends JpaRepository<RecordTagMapping, Long> {
}
