package io.driver.codrive.modules.mappings.recordCategoryMapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordCategoryMappingRepository extends JpaRepository<RecordCategoryMapping, Long> {
}
