package io.driver.codrive.modules.record.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.BoardDetailDto;
import io.driver.codrive.modules.record.model.BoardResponse;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final Map<String, Long> board = new LinkedHashMap<>();
	private final RecordRepository recordRepository;

	public List<Record> getRecordsByDate(User user, String pivotDate) {
		LocalDate pivot = DateUtils.getPivotDateOrToday(pivotDate);
		return recordRepository.getRecordsByDate(user.getUserId(), pivot);
	}

	private List<BoardDetailDto> getBoardDetailDtos(User user, Period period, String pivotDate) {
		LocalDate pivot = DateUtils.getPivotDateOrToday(pivotDate);

		if (period == Period.MONTHLY) {
			createMonthlyBoard(pivot);
			return recordRepository.getRecordCountByMonth(user.getUserId(), pivot);
		} else { //WEEKLY
			createWeeklyBoard(pivot);
			return recordRepository.getRecordCountByWeek(user.getUserId(), pivot);
		}
	}

	private void createMonthlyBoard(LocalDate pivotDate) {
		board.clear();
		int lastDay = YearMonth.from(pivotDate).lengthOfMonth();
		for (int day = 1; day <= lastDay; day++) {
			board.put(String.valueOf(day), 0L);
		}
	}

	private void createWeeklyBoard(LocalDate pivotDate) {
		board.clear();
		DayOfWeek dayOfWeek = pivotDate.getDayOfWeek();
		LocalDate monday = pivotDate.minusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());

		for (int day = 0; day < 7; day++) {
			LocalDate plusDay = monday.plusDays(day);
			board.put(String.valueOf(plusDay.getDayOfMonth()), 0L);
		}
	}

	private void updateBoard(List<BoardDetailDto> boardDetailDtos) {
		boardDetailDtos.forEach(dto -> board.put(dto.getDate(), dto.getCount()));
	}

	public List<BoardResponse> getBoardResponse(User user, Period period, String pivotDate) {
		List<BoardDetailDto> boardDetails = getBoardDetailDtos(user, period, pivotDate);
		updateBoard(boardDetails);
		return board.entrySet().stream()
			.map(entry -> BoardResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}
}
