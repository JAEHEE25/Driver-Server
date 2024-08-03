package io.driver.codrive.modules.record.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, RecordRepositoryCustom {
	Page<Record> findAllByUserAndStatus(User user, Status status, Pageable pageable);
}
