package com.myweb.chat.config;

import com.myweb.chat.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> {})
                .exceptionHandling(ex -> ex
                        // Khi chưa đăng nhập hoặc token không hợp lệ → 401 + JSON body
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            String body = String.format("{\"status\":%d,\"error\":\"Chưa xác thực\",\"message\":\"Bạn chưa đăng nhập hoặc token không hợp lệ\",\"path\":\"%s\"}",
                                    HttpStatus.UNAUTHORIZED.value(), request.getRequestURI());
                            response.getWriter().write(body);
                        })
                        // Khi đã đăng nhập nhưng không đủ quyền → 403 + JSON body
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            String body = String.format("{\"status\":%d,\"error\":\"Không có quyền truy cập\",\"message\":\"Bạn không có quyền thực hiện hành động này\",\"path\":\"%s\"}",
                                    HttpStatus.FORBIDDEN.value(), request.getRequestURI());
                            response.getWriter().write(body);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // WebSocket/SockJS endpoints
                        .requestMatchers("/ws/**").permitAll()

                        // Spring Boot error controller
                        .requestMatchers("/error").permitAll()

                        // Public Auth APIs
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/signup/admin", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Auth API
                        .requestMatchers("/api/auth/**").permitAll()

                        // User self endpoints: cho phép user đã đăng nhập
                        .requestMatchers("/api/users/me").authenticated()

                        // Các endpoint quản trị user: chỉ ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Admin API khác (nếu có)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Các API còn lại: yêu cầu đăng nhập
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Origin", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Sử dụng http.cors() + CorsConfigurationSource là đủ, không đăng ký CorsFilter thủ công để tránh bean conflict
}