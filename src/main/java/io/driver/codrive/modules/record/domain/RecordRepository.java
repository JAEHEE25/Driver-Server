package io.driver.codrive.modules.record.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, RecordRepositoryCustom {
	List<Record> findAllByUser(User user);

	List<Record> findAllByUserAndStatus(User user, Status status);
}
