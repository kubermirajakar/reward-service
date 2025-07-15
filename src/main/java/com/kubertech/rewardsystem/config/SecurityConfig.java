package com.kubertech.rewardsystem.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class that defines the security settings for the Reward System application.
 * <p>
 * This setup includes HTTP Basic authentication, disables CSRF protection,
 * and restricts access to reward-related API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * - Disables CSRF protection.<br>
     * - Secures "/api/rewards/**" endpoints, requiring authentication.<br>
     * - Allows all other requests without authentication.<br>
     * - Returns an HTTP 401 Unauthorized status for unauthenticated access attempts.
     *
     * @param http the {@link HttpSecurity} object used to configure web-based security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception in case of any configuration error
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/rewards/**")
                        .authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic ->
                        httpBasic.authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
                        })
                );

        return http.build();
    }
}