package io.driver.codrive.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtils {
	public Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal().toString().equals("anonymousUser")) {
			return null;
		}
		return Long.valueOf(authentication.getPrincipal().toString());
	}
}
