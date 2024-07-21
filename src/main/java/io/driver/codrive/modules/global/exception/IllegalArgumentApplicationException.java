package io.driver.codrive.modules.global.exception;

public class IllegalArgumentApplicationException extends ApplicationException {
	public IllegalArgumentApplicationException(String message) {
		super(ErrorType.BAD_REQUEST, message);
	}
}
