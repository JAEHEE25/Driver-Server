package io.driver.codrive.modules.record.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountBoardService {
	private final RecordRepository recordRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public BoardResponse getRecordsBoard(Long userId, String requestPivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		return getBoardResponse(user, pivotDate);
	}

	@Transactional(readOnly = true)
	protected BoardResponse getBoardResponse(User user, LocalDate pivotDate) {
		Map<String, Long> countBoard = updateCountBoard(user, Period.MONTHLY, pivotDate,
			createMonthlyCountBoard(pivotDate));
		int totalCount = getTotalCount(countBoard);
		int longestPeriod = getLongestPeriod(countBoard);
		int maxCount = getMaxCount(countBoard);
		List<BoardResponse.RecordSolvedResponse> board = getRecordSolvedBoard(countBoard);
		return BoardResponse.of(totalCount, longestPeriod, maxCount, board);
	}

	@Transactional(readOnly = true)
	protected Map<String, Long> updateCountBoard(User user, Period period, LocalDate pivotDate,
		Map<String, Long> countBoard) {
		List<RecordCountDto> recordCountDtos = getRecordCountDtos(user, period, pivotDate);
		if (recordCountDtos != null) {
			recordCountDtos.forEach(dto -> countBoard.put(dto.getDate(), dto.getCount()));
		}
		return countBoard;
	}

	@Transactional(readOnly = true)
	protected List<RecordCountDto> getRecordCountDtos(User user, Period period, LocalDate pivotDate) {
		if (period == Period.YEARLY) {
			return recordRepository.getYearlyRecordCountBoard(user.getUserId(), pivotDate);
		} else if (period == Period.MONTHLY) {
			return recordRepository.getMonthlyRecordCountBoard(user.getUserId(), pivotDate);
		} else {
			return null;
		}
	}

	private Map<String, Long> createYearlyCountBoard() {
		Map<String, Long> countBoard = new ConcurrentHashMap<>();
		for (int month = 1; month <= 12; month++) {
			countBoard.put(String.valueOf(month), 0L);
		}
		return countBoard;
	}

	private Map<String, Long> createMonthlyCountBoard(LocalDate pivotDate) {
		Map<String, Long> countBoard = new ConcurrentHashMap<>();
		int lastDay = YearMonth.from(pivotDate).lengthOfMonth();
		for (int day = 1; day <= lastDay; day++) {
			countBoard.put(String.valueOf(day), 0L);
		}
		return countBoard;
	}

	private int getTotalCount(Map<String, Long> countBoard) {
		return countBoard.values().stream().mapToInt(Long::intValue).sum();
	}

	private int getLongestPeriod(Map<String, Long> countBoard) {
		int longestPeriod = 0;
		int currentPeriod = 0;

		List<Integer> sortedKeys = countBoard.keySet().stream()
			.map(Integer::parseInt)
			.sorted()
			.toList();
		log.info("sortedKeys: {}", sortedKeys);

		for (Integer key : sortedKeys) {
			Long count = countBoard.get(key.toString());
			if (count > 0) {
				currentPeriod++;
			} else {
				longestPeriod = Math.max(longestPeriod, currentPeriod);
				currentPeriod = 0;
			}
		}
		return Math.max(longestPeriod, currentPeriod);
	}

	private int getMaxCount(Map<String, Long> countBoard) {
		return countBoard.values().stream().mapToInt(Long::intValue).max().orElse(0);
	}

	private List<BoardResponse.RecordSolvedResponse> getRecordSolvedBoard(Map<String, Long> countBoard) {
		return countBoard.entrySet().stream()
			.sorted(Comparator.comparingInt(entry -> Integer.parseInt(entry.getKey())))
			.map(entry -> BoardResponse.RecordSolvedResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Transactional(readOnly = true)
	public RecordMonthListResponse getRecordsByMonth(Long userId, SortType sortType, String requestPivotDate,
		Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		User user = userService.getUserById(userId);
		User currentUser = userService.getUserById(AuthUtils.getCurrentUserId());
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		Page<Record> records = recordRepository.getMonthlyRecords(user.getUserId(), pivotDate, sortType, pageable);
		return RecordMonthListResponse.of(records.getTotalPages(), records, user, currentUser.isFollowing(user));
	}

	@Transactional(readOnly = true)
	public UnsolvedMonthResponse getUnsolvedMonths(Long userId, String requestPivotDate) {
		User user = userService.getUserById(userId);
		LocalDate pivotDate = DateUtils.getPivotDateOrToday(requestPivotDate);
		Map<String, Long> countBoard = updateCountBoard(user, Period.YEARLY, pivotDate, createYearlyCountBoard());
		List<Integer> unsolvedMonths = countBoard.entrySet().stream()
			.filter(entry -> entry.getValue() == 0)
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> Integer.parseInt(entry.getKey())).sorted().collect(Collectors.toList());
		return UnsolvedMonthResponse.of(unsolvedMonths);
	}
}
