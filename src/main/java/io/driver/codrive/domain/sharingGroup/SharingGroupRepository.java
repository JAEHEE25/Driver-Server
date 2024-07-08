package io.driver.codrive.domain.sharingGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharingGroupRepository extends JpaRepository<SharingGroup, Long> {
}
