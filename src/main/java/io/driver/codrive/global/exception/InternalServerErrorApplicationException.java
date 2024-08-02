package io.driver.codrive.global.exception;

public class InternalServerErrorApplicationException extends ApplicationException {
	public InternalServerErrorApplicationException(String message) {
		super(ErrorType.INTERNAL_SERVER_ERROR, message);
	}
}
