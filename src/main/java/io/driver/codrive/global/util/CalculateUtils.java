package io.driver.codrive.global.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CalculateUtils {
	public static Integer calculateSuccessRate(Integer solvedDayCountByWeek) {
		return switch (solvedDayCountByWeek) {
			case 0 -> 0;
			case 1 -> 15;
			case 2 -> 30;
			case 3 -> 45;
			case 4 -> 60;
			case 5 -> 75;
			case 6 -> 90;
			default -> 100;
		};
	}
}
