package io.driver.codrive.global.auth;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";
	private final JwtProvider jwtProvider;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		SecurityContextHolder.clearContext();
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		String accessToken = resolveToken(httpServletRequest);

		try {
			if (StringUtils.hasText(accessToken)) {
				Claims claims = jwtProvider.getClaims(accessToken);

				if (claims != null && isValidToken(claims)) {
					Long userId = Long.valueOf(claims.getSubject());
					Authentication authentication = AuthenticationToken.getAuthentication(userId, accessToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);

					log.info("{} 인증 정보 저장, requestURI: {}", authentication.getName(),
						httpServletRequest.getRequestURI());
				}
			}
		} catch (ExpiredJwtException e) {
			log.warn("Invalid or missing JWT for requestURI: {}", httpServletRequest.getRequestURI());
			servletRequest.setAttribute("exception", e);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private boolean isValidToken(Claims claims) {
		return claims.getExpiration().after(new Date());
	}
}
