package io.driver.codrive.modules.global.config;

import org.springframework.context.annotation.Configuration;

import io.driver.codrive.modules.global.jwt.JwtAuthenticationFilter;
import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.ErrorResponse;
import io.driver.codrive.modules.global.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] WHITELIST = new String[] {
            APIConstants.API_PREFIX + "/auth/**",
            "/favicon.ico",
            "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtConfig jwtConfig) throws Exception {
        http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
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
}
