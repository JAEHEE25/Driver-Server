package io.driver.codrive.global.exception;

public class NotFoundApplcationException extends ApplicationException {
	public NotFoundApplcationException(String message) {
		super(ErrorType.NOT_FOUND, message);
	}
}