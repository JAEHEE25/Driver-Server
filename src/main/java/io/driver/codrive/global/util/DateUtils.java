package io.driver.codrive.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

	private LocalDate parsePivotDate(String pivotDate) {
		return LocalDate.parse(pivotDate);
	}

	public LocalDate getPivotDateOrToday(String pivotDate) {
		if (pivotDate == null) {
			return LocalDate.now();
		}
		return parsePivotDate(pivotDate);
	}

	public static String formatYearMonth(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }
}
