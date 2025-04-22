package com.gdgswu.planeat.global.config;

import com.gdgswu.planeat.domain.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity // spring security 설정 직접 커스터마이징
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // REST API는 CSRF 보호 불필요, 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS)) // jwt 기반 -> 세션 x
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/foods", "/api/foods/**").permitAll() // 로그인/회원가입은 인증없이 허용
                        .anyRequest().authenticated() // 그 외 요청은 JWT 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 커스텀 JWT필터를 스프링 시큐리티 앞단에 연결
                .build();
    }
}
