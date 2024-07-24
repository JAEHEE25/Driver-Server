package io.driver.codrive.modules.global.exception.handler;

import io.driver.codrive.modules.global.exception.ApplicationException;
import io.driver.codrive.modules.global.exception.ErrorType;

public class ForbiddenApplcationException extends ApplicationException {
	public ForbiddenApplcationException(String message) {
		super(ErrorType.FORBIDDEN, message);
	}
}