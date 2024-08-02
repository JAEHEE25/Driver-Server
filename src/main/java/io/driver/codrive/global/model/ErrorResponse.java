package io.driver.codrive.global.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private int code;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> metadata;

	public static ErrorResponse of(int code, String message, Map<String, Object> metadata) {
		return ErrorResponse.builder()
			.code(code)
			.message(message)
			.metadata(metadata)
			.build();
	}

}