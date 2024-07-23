package io.driver.codrive.modules.global.exception;

public class InternalServerErrorApplicationException extends ApplicationException {
	public InternalServerErrorApplicationException(String message) {
		super(ErrorType.INTERNAL_SERVER_ERROR, message);
	}
}
