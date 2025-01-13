package io.driver.codrive.global.exception;

public class ForbiddenApplicationException extends ApplicationException {
	public ForbiddenApplicationException(String message) {
		super(ErrorType.FORBIDDEN, message);
	}
}