package io.driver.codrive.modules.record.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepositoryCustom {
	List<Record> getRecordsByDate(Long userId, LocalDate pivotDate);
}
