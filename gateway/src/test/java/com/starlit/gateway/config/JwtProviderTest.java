package com.starlit.gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static final String SECRET = "starlit-jwt-secret-key-for-development-only";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", SECRET);
        jwtProvider.init();
    }

    /** user-service가 발급하는 것과 동일한 형식의 토큰을 생성하는 헬퍼 */
    private String createTestToken(Long userId, String email, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("유효한 토큰 검증 - true를 반환한다")
    void validateToken_validToken_returnsTrue() {
        // given
        String token = createTestToken(1L, "test@example.com", 86400000);

        // when & then
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 - false를 반환한다")
    void validateToken_expiredToken_returnsFalse() {
        // given
        String token = createTestToken(1L, "test@example.com", -1000);

        // when & then
        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("변조된 토큰 검증 - false를 반환한다")
    void validateToken_tamperedToken_returnsFalse() {
        // given
        String token = createTestToken(1L, "test@example.com", 86400000) + "tampered";

        // when & then
        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("토큰에서 userId를 추출한다")
    void getUserId_returnsCorrectUserId() {
        // given
        String token = createTestToken(42L, "test@example.com", 86400000);

        // when
        Long userId = jwtProvider.getUserId(token);

        // then
        assertThat(userId).isEqualTo(42L);
    }

    @Test
    @DisplayName("토큰에서 email을 추출한다")
    void getEmail_returnsCorrectEmail() {
        // given
        String token = createTestToken(1L, "test@example.com", 86400000);

        // when
        String email = jwtProvider.getEmail(token);

        // then
        assertThat(email).isEqualTo("test@example.com");
    }
}
