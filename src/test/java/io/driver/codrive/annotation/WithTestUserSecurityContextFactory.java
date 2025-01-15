package io.driver.codrive.annotation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import io.driver.codrive.global.auth.AuthenticationToken;

public class WithTestUserSecurityContextFactory implements WithSecurityContextFactory<WithTestUser> {
	@Override
	public SecurityContext createSecurityContext(WithTestUser annotation) {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

		Long userId = annotation.userId();
		String token = annotation.token();

		Authentication authentication = AuthenticationToken.getAuthentication(userId, token);
		securityContext.setAuthentication(authentication);
		return securityContext;
	}
}
