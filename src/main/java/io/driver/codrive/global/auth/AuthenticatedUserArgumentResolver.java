package io.driver.codrive.global.auth;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.driver.codrive.global.util.AuthUtils;
import io.driver.codrive.modules.user.domain.User;
import io.driver.codrive.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class) && parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Long authenticatedUserId = AuthUtils.getCurrentUserId();
        if (authenticatedUserId == null) {
            return null;
        }
        return userService.getUserById(authenticatedUserId);
    }
}
