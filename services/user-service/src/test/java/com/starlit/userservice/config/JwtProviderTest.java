package com.starlit.userservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", "starlit-jwt-secret-key-for-development-only");
        ReflectionTestUtils.setField(jwtProvider, "expiration", 86400000L);
        jwtProvider.init();
    }

    @Test
    @DisplayName("토큰 생성 - 유효한 JWT 문자열이 반환된다")
    void createToken_returnsValidJwtString() {
        // when
        String token = jwtProvider.createToken(1L, "test@example.com");

        // then
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰에서 userId 추출 - 생성 시 넣은 userId가 반환된다")
    void getUserId_returnsCorrectUserId() {
        // given
        String token = jwtProvider.createToken(42L, "test@example.com");

        // when
        Long userId = jwtProvider.getUserId(token);

        // then
        assertThat(userId).isEqualTo(42L);
    }

    @Test
    @DisplayName("토큰 검증 - 유효한 토큰은 true를 반환한다")
    void validateToken_validToken_returnsTrue() {
        // given
        String token = jwtProvider.createToken(1L, "test@example.com");

        // when & then
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰 검증 - 만료된 토큰은 false를 반환한다")
    void validateToken_expiredToken_returnsFalse() {
        // given
        JwtProvider shortLivedProvider = new JwtProvider();
        ReflectionTestUtils.setField(shortLivedProvider, "secret", "starlit-jwt-secret-key-for-development-only");
        ReflectionTestUtils.setField(shortLivedProvider, "expiration", -1000L);
        shortLivedProvider.init();

        String token = shortLivedProvider.createToken(1L, "test@example.com");

        // when & then
        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 - 변조된 토큰은 false를 반환한다")
    void validateToken_tamperedToken_returnsFalse() {
        // given
        String token = jwtProvider.createToken(1L, "test@example.com");
        String tamperedToken = token + "tampered";

        // when & then
        assertThat(jwtProvider.validateToken(tamperedToken)).isFalse();
    }
}
