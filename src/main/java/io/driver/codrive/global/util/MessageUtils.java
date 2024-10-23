package io.driver.codrive.global.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtils {
	private static final String LONG_NAME_FORMAT = "%s..";

	public static String changeNameFormat(String name, int length) {
		if (name.length() > length) {
			return String.format(LONG_NAME_FORMAT, name.substring(0, length));
		}
		return name;
	}
}
