package io.driver.codrive.modules.global.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.driver.codrive.modules.global.exception.ApplicationException;
import io.driver.codrive.modules.global.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e) {
		log.error("ApplicationException: {}", e.getMessage());
		ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatusCode.valueOf(e.getCode()));
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errorDetails.put(e.getField(), e.getRejectedValue()));
        return new ResponseEntity<>(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.", errorDetails), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return new ResponseEntity<>(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
