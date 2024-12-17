package io.driver.codrive.global.exception;

public class NotFoundApplicationException extends ApplicationException {
	public NotFoundApplicationException(String message) {
		super(ErrorType.NOT_FOUND, message);
	}
}