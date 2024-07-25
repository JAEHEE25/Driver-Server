package io.driver.codrive.modules.global.util;

import java.time.LocalDate;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

	public LocalDate getLocalDateByString(String pivotDate) {
		return LocalDate.parse(pivotDate);
	}
}
