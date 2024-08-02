package io.driver.codrive.global.jwt;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.driver.codrive.global.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
	private final SecretKey secretKey;

	public JwtAuthenticationFilter(JwtConfig jwtConfig) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtConfig.getSecretKey()));
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		SecurityContextHolder.clearContext();
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String accessToken = resolveToken(httpServletRequest);

		if (StringUtils.hasText(accessToken)) {
			Claims claims = getClaims(accessToken);

			if (isValidToken(claims)) {
				Long userId = Long.valueOf(claims.getSubject());
				Authentication authentication = AuthenticationToken.getAuthentication(userId, accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.info("{} 인증 정보 저장, requestURI: {}", authentication.getName(), httpServletRequest.getRequestURI());
			}
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

	private Claims getClaims(String accessToken) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();
	}

	private boolean isValidToken(Claims claims) {
		return claims.getExpiration().after(new Date());
	}
}
