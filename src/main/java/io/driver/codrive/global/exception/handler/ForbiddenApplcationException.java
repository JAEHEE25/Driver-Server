package io.driver.codrive.global.exception.handler;

import io.driver.codrive.global.exception.ApplicationException;
import io.driver.codrive.global.exception.ErrorType;

public class ForbiddenApplcationException extends ApplicationException {
	public ForbiddenApplcationException(String message) {
		super(ErrorType.FORBIDDEN, message);
	}
}