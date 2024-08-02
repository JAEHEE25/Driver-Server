package io.driver.codrive.modules.record.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.record.model.BoardDetailDto;

@Repository
public interface RecordRepositoryCustom {
	List<Record> getSavedRecordsByDate(Long userId, LocalDate pivotDate);
	List<BoardDetailDto> getSavedRecordCountByMonth(Long userId, LocalDate pivotDate);
	List<BoardDetailDto> getSavedRecordCountByWeek(Long userId, LocalDate pivotDate);


}
