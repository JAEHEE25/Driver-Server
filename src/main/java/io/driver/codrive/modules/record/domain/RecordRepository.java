package io.driver.codrive.modules.record.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.user.domain.User;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, RecordRepositoryCustom {
	List<Record> findAllByUserAndRecordStatus(User user, RecordStatus recordStatus);
	Page<Record> findAllByUserAndRecordStatusOrderByCreatedAtDesc(User user, RecordStatus recordStatus, Pageable pageable);
	List<Record> findAllByUserAndRecordStatusOrderByCreatedAtDesc(User user, RecordStatus recordStatus);
	List<Record> findAllByUserAndRecordStatusAndCreatedAtBetween(User user, RecordStatus recordStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);

	@Query("SELECT r.user.userId FROM Record r WHERE r.recordId = :recordId")
	Long findOwnerIdByRecordId(@Param("recordId") Long recordId);
}
