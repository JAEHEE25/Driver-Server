package io.driver.codrive.global.exception;

public class UnauthorizedApplicationException extends ApplicationException {
	public UnauthorizedApplicationException(String message) {
		super(ErrorType.UNAUTHORIZED, message);
	}
}
