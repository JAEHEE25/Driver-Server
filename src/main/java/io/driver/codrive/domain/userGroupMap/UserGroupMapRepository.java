package io.driver.codrive.domain.userGroupMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupMapRepository extends JpaRepository<UserGroupMap, Long> {
}
