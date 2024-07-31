package io.driver.codrive.global.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseResponse<T> {
	private Integer code;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	public static <T> BaseResponse<T> of(T data) {
		return BaseResponse.<T>builder()
			.code(200)
			.message("SUCCESS")
			.data(data)
			.build();
	}
}
