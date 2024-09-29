package io.driver.codrive.global.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TemplateUtils {
	private static final String NEW_LINE_TAG = "<br>";

	public static String formatTemplate(String template, Map<String, String> parameters) {
		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				template = template.replace(String.format("${%s}", entry.getKey()), entry.getValue());
			}
		}
		return template;
	}

	public static String readTemplate(String path) throws IOException {
		Resource resource = new ClassPathResource(path);
		byte[] arr = FileCopyUtils.copyToByteArray(resource.getInputStream());
		return new String(arr, StandardCharsets.UTF_8);
	}

	public static String encodeBase64(String original) {
 		return Base64.getEncoder().encodeToString(original.getBytes());
	}

	public static String replaceNewLineTag(String memo) {
		if (memo != null && !memo.isEmpty()) {
			return memo.replace("\n", NEW_LINE_TAG);
		}
		return memo;
	}
}
