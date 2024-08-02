package io.driver.codrive.global.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final int code;
    private final String message;

    public ApplicationException(ErrorType errorType, String message) {
        this.code = errorType.getCode();
        this.message = errorType.formatMessage(message);
    }
}