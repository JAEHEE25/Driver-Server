package io.driver.codrive.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
	BAD_REQUEST(400, "%s"),
	UNAUTHORIZED(401, "%s"),
	FORBIDDEN(403, "%s에 대한 권한이 없습니다."),
	NOT_FOUND(404, "%s을/를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "%s"),
	ALREADY_EXISTS(409, "%s이/가 이미 존재합니다.");

	private final int code;

	private final String message;

	public String formatMessage(Object... args) {
		return String.format(message, args);
	}
}
