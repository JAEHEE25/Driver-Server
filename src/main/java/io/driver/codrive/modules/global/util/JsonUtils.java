package io.driver.codrive.modules.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();

	public String serialize(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}
}
