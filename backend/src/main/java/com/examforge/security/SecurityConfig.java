package com.examforge.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Stateless API security. Only public endpoints are listed here; every feature endpoint is
 * protected by {@code anyRequest().authenticated()} plus method-level {@code @PreAuthorize}, so
 * feature work never needs to edit this class. JWT resource-server support is added in S03.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**",
        "/api/v1/time",
        "/api/actuator/health",
        "/api/actuator/health/**",
        "/api/actuator/info",
        "/api/v3/api-docs/**",
        "/api/swagger-ui.html",
        "/api/swagger-ui/**",
        "/error"
    };

    @Bean
    SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver)
            throws Exception {
        // Delegate 401/403 rendering to GlobalExceptionHandler so every error has the same shape.
        var problemHandlers = new ProblemSecurityHandlers(resolver);
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(
                        e -> e.authenticationEntryPoint(problemHandlers).accessDeniedHandler(problemHandlers));
        return http.build();
    }
}
