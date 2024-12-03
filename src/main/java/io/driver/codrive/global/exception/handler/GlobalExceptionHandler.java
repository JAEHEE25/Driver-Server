package io.driver.codrive.global.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.StaleStateException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.driver.codrive.global.exception.ApplicationException;
import io.driver.codrive.global.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e, HttpServletRequest request) {
		log.error("Request: {} {}, ApplicationException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
		ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatusCode.valueOf(e.getCode()));
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errorDetails.put(e.getField(), e.getDefaultMessage()));
        return new ResponseEntity<>(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.", errorDetails), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Request: {} {}, Exception: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return new ResponseEntity<>(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponse> handleStaleStateException(ConcurrencyFailureException e, HttpServletRequest request) {
        log.error("Request: {} {}, ConcurrencyFailureException: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return new ResponseEntity<>(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "요청을 처리할 수 없습니다.", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
