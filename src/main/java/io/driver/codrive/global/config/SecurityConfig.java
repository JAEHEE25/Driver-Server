package io.driver.codrive.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import io.driver.codrive.global.constants.APIConstants;
import io.driver.codrive.global.util.JsonUtils;
import io.driver.codrive.global.jwt.JwtAuthenticationFilter;
import io.driver.codrive.global.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] WHITELIST = new String[] {
            APIConstants.API_PREFIX + "/auth/**",
            APIConstants.API_PREFIX + "/test/**",
			APIConstants.API_PREFIX + "/records/{recordId}",
            "/favicon.ico",
            "/error",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtConfig jwtConfig) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(custom -> custom.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(e -> {
                e.requestMatchers(WHITELIST).permitAll();
                e.anyRequest().authenticated();
            })
            .exceptionHandling(e -> {
                e.authenticationEntryPoint((request, response, ex) -> {
                    log.error("인증되지 않은 요청입니다. URI: [{}]", request.getRequestURI());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(JsonUtils.serialize(
						ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Unauthroized Request", null)));
                });

                e.accessDeniedHandler((request, response, ex) -> {
                    log.error("접근 권한이 없습니다. URI: [{}]", request.getRequestURI());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(JsonUtils.serialize(ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "Access Denied", null)));
                });
            });

        http.addFilterAfter(new JwtAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

   	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:63342", "http://localhost:3000",
			"https://driver-client.pages.dev", "https://api.codrive.co.kr", "https://www.codrive.co.kr"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(
			List.of("Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method",
				"Access-Control-Request-Headers", "Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);
        return request -> configuration;
	}
}
