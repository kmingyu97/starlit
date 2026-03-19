package com.starlit.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 *
 * <p>user-service는 자체 인증 필터를 사용하지 않는다.
 * 인증/인가는 Gateway에서 JWT 검증 후 처리하므로, 여기서는 모든 요청을 허용한다.
 * BCryptPasswordEncoder만 빈으로 등록하여 회원가입·로그인 시 비밀번호 해싱에 사용한다.</p>
 */
@Configuration
public class SecurityConfig {

    /** 보안 필터 체인 — CSRF, 폼 로그인, HTTP Basic 비활성화 후 모든 요청 허용. */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /** BCrypt 기반 비밀번호 인코더. 회원가입 시 해싱, 로그인 시 비교에 사용된다. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
