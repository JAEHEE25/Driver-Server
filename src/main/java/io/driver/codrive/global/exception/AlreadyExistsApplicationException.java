package io.driver.codrive.global.exception;

public class AlreadyExistsApplicationException extends ApplicationException {
	public AlreadyExistsApplicationException(String message) {
		super(ErrorType.ALREADY_EXISTS, message);
	}
}
