package com.starlit.gateway.filter;

import com.starlit.gateway.config.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {

    private static final String SECRET = "starlit-jwt-secret-key-for-development-only";

    private HandlerFilterFunction<ServerResponse, ServerResponse> filter;
    private final HandlerFunction<ServerResponse> okHandler = request -> ServerResponse.ok().build();

    @BeforeEach
    void setUp() {
        JwtProvider jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", SECRET);
        jwtProvider.init();

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtProvider);
        filter = jwtAuthFilter.filter();
    }

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

    private ServerRequest createRequest(String method, String path) {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest(method, path);
        return ServerRequest.create(httpRequest, Collections.emptyList());
    }

    private ServerRequest createRequestWithAuth(String method, String path, String token) {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest(method, path);
        httpRequest.addHeader("Authorization", "Bearer " + token);
        return ServerRequest.create(httpRequest, Collections.emptyList());
    }

    // === 공개 API 테스트 ===

    @Test
    @DisplayName("POST /api/users/login - 공개 API는 JWT 없이 통과한다")
    void publicApi_login_passesWithoutJwt() throws Exception {
        ServerRequest request = createRequest("POST", "/api/users/login");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /api/users/signup - 공개 API는 JWT 없이 통과한다")
    void publicApi_signup_passesWithoutJwt() throws Exception {
        ServerRequest request = createRequest("POST", "/api/users/signup");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET /api/stocks/indices - 공개 API는 JWT 없이 통과한다")
    void publicApi_stocks_passesWithoutJwt() throws Exception {
        ServerRequest request = createRequest("GET", "/api/stocks/indices");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
    }

    // === 인증 필요 API 테스트 ===

    @Test
    @DisplayName("GET /api/users/me - Authorization 헤더 없으면 401 반환")
    void protectedApi_noAuthHeader_returns401() throws Exception {
        ServerRequest request = createRequest("GET", "/api/users/me");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET /api/users/me - 잘못된 토큰이면 401 반환")
    void protectedApi_invalidToken_returns401() throws Exception {
        ServerRequest request = createRequestWithAuth("GET", "/api/users/me", "invalid.jwt.token");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET /api/users/me - 만료된 토큰이면 401 반환")
    void protectedApi_expiredToken_returns401() throws Exception {
        String expiredToken = createTestToken(1L, "test@example.com", -1000);
        ServerRequest request = createRequestWithAuth("GET", "/api/users/me", expiredToken);

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET /api/users/me - 유효한 토큰이면 X-User-Id, X-User-Email 헤더가 주입된다")
    void protectedApi_validToken_addsHeaders() throws Exception {
        String token = createTestToken(42L, "test@example.com", 86400000);
        ServerRequest request = createRequestWithAuth("GET", "/api/users/me", token);

        AtomicReference<ServerRequest> captured = new AtomicReference<>();
        HandlerFunction<ServerResponse> capturingHandler = req -> {
            captured.set(req);
            return ServerResponse.ok().build();
        };

        ServerResponse response = filter.filter(request, capturingHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
        assertThat(captured.get().headers().firstHeader("X-User-Id")).isEqualTo("42");
        assertThat(captured.get().headers().firstHeader("X-User-Email")).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("POST /api/community/posts - 비공개 API는 JWT 필요")
    void protectedApi_postCommunity_requiresJwt() throws Exception {
        ServerRequest request = createRequest("POST", "/api/community/posts");

        ServerResponse response = filter.filter(request, okHandler);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
