package io.driver.codrive.modules.record.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.driver.codrive.modules.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.BoardDetailDto;
import io.driver.codrive.modules.record.model.BoardResponse;
import io.driver.codrive.modules.record.model.RecordBoardRequest;
import io.driver.codrive.modules.record.model.RecordListRequest;
import io.driver.codrive.modules.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final Map<String, Long> board = new LinkedHashMap<>();
	private final RecordRepository recordRepository;

	public List<Record> getRecordsByDate(User user, RecordListRequest request) {
		if (request.pivotDate() == null) {
			return recordRepository.findAllByUser(user);
		}
		return recordRepository.getRecordsByDate(user.getUserId(), DateUtils.parsePivotDate(request.pivotDate()));
	}

	private List<BoardDetailDto> getBoardDetailDtos(User user, Period period, RecordBoardRequest request) {
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(request.pivotDate());

		if (period == Period.MONTHLY) {
			createMonthlyBoard(pivotDate);
			return recordRepository.getRecordCountByMonth(user.getUserId(), pivotDate);
		} else { //WEEKLY
			createWeeklyBoard(pivotDate);
			return recordRepository.getRecordCountByWeek(user.getUserId(), pivotDate.atTime(23, 59, 59));
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

	public List<BoardResponse> getBoardResponse(User user, Period period, RecordBoardRequest request) {
		List<BoardDetailDto> boardDetails = getBoardDetailDtos(user, period, request);
		updateBoard(boardDetails);
		return board.entrySet().stream()
			.map(entry -> BoardResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}
}
