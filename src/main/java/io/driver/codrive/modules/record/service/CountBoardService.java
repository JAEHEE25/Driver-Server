package io.driver.codrive.modules.record.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.RecordCountDto;
import io.driver.codrive.modules.record.model.RecordCountBoardResponse;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountBoardService {
	private final Map<String, Long> countBoard = new LinkedHashMap<>();
	private final RecordRepository recordRepository;

	public List<RecordCountBoardResponse.RecordCountResponse> getCountBoard(User user, Period period, LocalDate pivotDate) {
		List<RecordCountDto> countDtos = getRecordCountDtos(user, period, pivotDate);
		updateCountBoard(countDtos);
		return countBoard.entrySet().stream()
			.map(entry -> RecordCountBoardResponse.RecordCountResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}

	private List<RecordCountDto> getRecordCountDtos(User user, Period period, LocalDate pivotDate) {
		if (period == Period.MONTHLY) {
			createMonthlyCountBoard(pivotDate);
			return recordRepository.getMonthlyRecordCountBoard(user.getUserId(), pivotDate);
		} else if (period == Period.WEEKLY) {
			createWeeklyCountBoard(pivotDate);
			return recordRepository.getWeeklyRecordCountBoard(user.getUserId(), pivotDate);
		} else {
			createYearlyCountBoard();
			return recordRepository.getYearlyRecordCount(user.getUserId(), pivotDate);
		}
	}

	private void updateCountBoard(List<RecordCountDto> recordCountDtos) {
		recordCountDtos.forEach(dto -> countBoard.put(dto.getDate(), dto.getCount()));
	}

	private void createYearlyCountBoard() {
		countBoard.clear();
		for (int month = 1; month <= 12; month++) {
			countBoard.put(String.valueOf(month), 0L);
		}
	}

	private void createMonthlyCountBoard(LocalDate pivotDate) {
		countBoard.clear();
		int lastDay = YearMonth.from(pivotDate).lengthOfMonth();
		for (int day = 1; day <= lastDay; day++) {
			countBoard.put(String.valueOf(day), 0L);
		}
	}

	private void createWeeklyCountBoard(LocalDate pivotDate) {
		countBoard.clear();
		DayOfWeek dayOfWeek = pivotDate.getDayOfWeek();
		LocalDate monday = pivotDate.minusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());

		for (int day = 0; day < 7; day++) {
			LocalDate plusDay = monday.plusDays(day);
			countBoard.put(String.valueOf(plusDay.getDayOfMonth()), 0L);
		}
	}
}
