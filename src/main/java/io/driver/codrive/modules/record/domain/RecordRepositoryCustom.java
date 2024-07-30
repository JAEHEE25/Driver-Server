package io.driver.codrive.modules.record.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.driver.codrive.modules.record.model.BoardDetailDto;

@Repository
public interface RecordRepositoryCustom {
	List<Record> getRecordsByDate(Long userId, LocalDate pivotDate);
	List<BoardDetailDto> getRecordCountByMonth(Long userId, LocalDate pivotDate);
	List<BoardDetailDto> getRecordCountByWeek(Long userId, LocalDate pivotDate);


}
