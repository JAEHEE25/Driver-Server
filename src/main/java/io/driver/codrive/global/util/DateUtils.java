package io.driver.codrive.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

	private LocalDate parsePivotDate(String pivotDate) {
		try {
			return LocalDate.parse(pivotDate);
		} catch (Exception e) {
			throw new IllegalArgumentApplicationException("날짜 형식이 잘못되었습니다.");
		}
	}

	public LocalDate getPivotDateOrToday(String pivotDate) {
		if (pivotDate == null) {
			return LocalDate.now();
		}
		return parsePivotDate(pivotDate);
	}

	public static String formatYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }

	public static String formatYearMonth(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }

	public static String formatCreatedAtByYMDHM(LocalDateTime createdAt) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH시 mm분");
		return createdAt.format(formatter);
	}

	public static String formatCreatedAtByMD(LocalDateTime createdAt) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M.d");
		return createdAt.format(formatter);
	}
}
