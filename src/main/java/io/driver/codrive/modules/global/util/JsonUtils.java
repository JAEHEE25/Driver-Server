package io.driver.codrive.modules.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.driver.codrive.modules.global.exception.InternalServerErrorApplicationException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();

	public String serialize(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorApplicationException("JSON 직렬화에 실패했습니다.");
		}
	}
}
