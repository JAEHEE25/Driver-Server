package io.driver.codrive.modules.global.constants;

import lombok.Getter;

@Getter
public class APIConstants {
	private static final String VERSION = "/v1";
	public static final String API_PREFIX = "/api" + VERSION;
	public static final String PUBLIC_API_PREFIX = "/public" + API_PREFIX;
}