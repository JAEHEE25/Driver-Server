package io.driver.codrive.domain.groupRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {
}
