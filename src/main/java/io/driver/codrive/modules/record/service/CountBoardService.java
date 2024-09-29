package io.driver.codrive.modules.record.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.driver.codrive.global.model.SortType;
import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.global.util.DateUtils;
import io.driver.codrive.modules.record.domain.Period;
import io.driver.codrive.modules.record.domain.Record;
import io.driver.codrive.modules.record.domain.RecordRepository;
import io.driver.codrive.modules.record.model.dto.RecordCountDto;
import io.driver.codrive.modules.record.model.response.BoardResponse;
import io.driver.codrive.modules.record.model.response.RecordMonthListResponse;
import io.driver.codrive.modules.record.model.response.UnsolvedMonthResponse;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountBoardService {
	private final Map<String, Long> countBoard = new LinkedHashMap<>();
	private final RecordRepository recordRepository;
	private final UserService userService;

	@Transactional
	public BoardResponse getRecordsBoard(Long userId, String requestPivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		return getBoardResponse(user, pivotDate);
	}

	private BoardResponse getBoardResponse(User user, LocalDate pivotDate) {
		updateCountBoard(user, Period.MONTHLY, pivotDate);
		int totalCount = getTotalCount();
		int longestPeriod = getLongestPeriod();
		int maxCount = getMaxCount();
		List<BoardResponse.RecordSolvedResponse> board = getRecordSolvedBoard();
		return BoardResponse.of(totalCount, longestPeriod, maxCount, board);
	}

	private void updateCountBoard(User user, Period period, LocalDate pivotDate) {
		updateCountBoardByDto(getRecordCountDtos(user, period, pivotDate));
	}

	private List<BoardResponse.RecordSolvedResponse> getRecordSolvedBoard() {
		return countBoard.entrySet().stream()
			.map(entry -> BoardResponse.RecordSolvedResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}

	private int getTotalCount() {
		return countBoard.values().stream().mapToInt(Long::intValue).sum();
	}

	private int getLongestPeriod() {
		int longestPeriod = 0;
		int currentPeriod = 0;

		for (Long count : countBoard.values()) {
			if (count > 0) {
				currentPeriod++;
			} else {
				longestPeriod = Math.max(longestPeriod, currentPeriod);
				currentPeriod = 0;
			}
		}
		return Math.max(longestPeriod, currentPeriod);
	}

	private int getMaxCount() {
		return countBoard.values().stream().mapToInt(Long::intValue).max().orElse(0);
	}

	@Transactional
	public RecordMonthListResponse getRecordsByMonth(Long userId, SortType sortType, String requestPivotDate, Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		User user = userService.getUserById(userId);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		Page<Record> records = recordRepository.getMonthlyRecords(user.getUserId(), pivotDate, sortType, pageable);
		return RecordMonthListResponse.of(records.getTotalPages(), records, user, currentUser.isFollowing(user));
	}

	@Transactional
	public UnsolvedMonthResponse getUnsolvedMonths(Long userId, String requestPivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		updateCountBoard(user, Period.YEARLY, pivotDate);
		List<Integer> unsolvedMonths = countBoard.entrySet().stream()
			.filter(entry -> entry.getValue() == 0)
			.map(entry -> Integer.parseInt(entry.getKey()))
			.collect(Collectors.toList());
		return UnsolvedMonthResponse.of(unsolvedMonths);
	}

	private List<RecordCountDto> getRecordCountDtos(User user, Period period, LocalDate pivotDate) {
		if (period == Period.YEARLY) {
			createYearlyCountBoard();
			return recordRepository.getYearlyRecordCountBoard(user.getUserId(), pivotDate);
		} else if (period == Period.MONTHLY) {
			createMonthlyCountBoard(pivotDate);
			return recordRepository.getMonthlyRecordCountBoard(user.getUserId(), pivotDate);
		} else {
			return null;
		}
	}

	private void updateCountBoardByDto(List<RecordCountDto> recordCountDtos) {
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

}
