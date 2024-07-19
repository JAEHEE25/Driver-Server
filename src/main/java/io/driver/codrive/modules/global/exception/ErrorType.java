package io.driver.codrive.modules.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
	ILLEGAL_ARGUMENT(400, "%s"),
	UNAUTHORIZED(401, "%s"),
	NOT_FOUND(404, "%s을/를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "%s");

	private final int code;

	private final String message;

	public String formatMessage(Object... args) {
		return String.format(message, args);
	}
}
