package io.driver.codrive.global.config;


import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.driver.codrive.global.auth.AuthenticatedUserArgumentResolver;
import io.driver.codrive.global.auth.AuthenticatedUserIdArgumentResolver;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final UserService userService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new AuthenticatedUserArgumentResolver(userService));
		resolvers.add(new AuthenticatedUserIdArgumentResolver());
	}
}
