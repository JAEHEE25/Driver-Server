package io.driver.codrive.modules.global.exception;

public class AlreadyExistsApplicationException extends ApplicationException {
	public AlreadyExistsApplicationException(String message) {
		super(ErrorType.ALREADY_EXISTS, message);
	}
}
