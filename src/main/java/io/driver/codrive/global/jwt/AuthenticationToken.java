package io.driver.codrive.global.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AuthenticationToken extends AbstractAuthenticationToken {
    private final Long userId;
    private final String credentials;

    private AuthenticationToken(Long userId, String token) {
        super(null);
        this.userId = userId;
        this.credentials = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return String.valueOf(userId);
    }

    public static AuthenticationToken getAuthentication(Long userId, String token) {
        return new AuthenticationToken(userId, token);
    }

}
