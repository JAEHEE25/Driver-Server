package io.driver.codrive.domain.tagRecordMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRecordMapRepository extends JpaRepository<TagRecordMap, Long> {
}
