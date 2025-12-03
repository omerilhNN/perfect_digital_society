package com.perfectdigitalsociety.config;

import com.perfectdigitalsociety.security.JwtAuthenticationEntryPoint;
import com.perfectdigitalsociety.security.JwtAuthenticationFilter;
import com.perfectdigitalsociety.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DAO authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable) // CORS handled by CorsConfig
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/balance/current").permitAll()
                        .requestMatchers("/api/community/rules").permitAll()
                        .requestMatchers("/api/community/metrics").permitAll()

                        // Health and monitoring endpoints
                        .requestMatchers("/api/health/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // API documentation endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()

                        // User endpoints - require authentication
                        .requestMatchers(HttpMethod.GET, "/api/users/profile/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/balance/**").authenticated()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/my-balance").authenticated()
                        .requestMatchers("/api/users/logout").authenticated()

                        // Message endpoints - require authentication
                        .requestMatchers("/api/messages/**").authenticated()

                        // Balance endpoints - most require authentication
                        .requestMatchers(HttpMethod.GET, "/api/balance/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/balance/trigger").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/balance/rebalance").hasRole("ADMIN")

                        // Community endpoints - mixed access
                        .requestMatchers(HttpMethod.POST, "/api/community/rules").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/rules/*/vote").authenticated()
                        .requestMatchers("/api/community/my-rules").authenticated()
                        .requestMatchers("/api/community/health-report").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers("/api/community/evaluate-rules").hasAnyRole("MODERATOR", "ADMIN")

                        // Admin endpoints - admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}