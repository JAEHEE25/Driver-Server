package io.driver.codrive.modules.global.exception;

public class NotFoundApplcationException extends ApplicationException {
	public NotFoundApplcationException(String message) {
		super(ErrorType.NOT_FOUND, message);
	}
}