package io.driver.codrive.modules.record.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.record.model.RecordCountDto;

@Repository
public interface RecordRepositoryCustom {
	List<Record> getSavedRecordsByDay(Long userId, LocalDate pivotDate);
	Page<Record> getSavedRecordsByMonth(Long userId, LocalDate pivotDate, Pageable pageable);
	List<RecordCountDto> getSavedRecordCountByMonth(Long userId, LocalDate pivotDate);
	List<RecordCountDto> getSavedRecordCountByWeek(Long userId, LocalDate pivotDate);


}
