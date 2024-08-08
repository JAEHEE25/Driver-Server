package io.driver.codrive.global.exception;

public class ForbiddenApplcationException extends ApplicationException {
	public ForbiddenApplcationException(String message) {
		super(ErrorType.FORBIDDEN, message);
	}
}