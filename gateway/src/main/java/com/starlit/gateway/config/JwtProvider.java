package com.starlit.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 토큰 검증·파싱 유틸리티 (Gateway 전용).
 *
 * <p>user-service에서 발급한 토큰을 동일한 시크릿 키(HS256)로 검증한다.
 * Gateway에서는 토큰을 발급하지 않으며, 검증과 클레임 추출만 담당한다.</p>
 *
 * <p>추출 가능한 정보:</p>
 * <ul>
 *   <li>subject → userId (Long)</li>
 *   <li>claim "email" → 사용자 이메일 (String)</li>
 * </ul>
 */
@Component
public class JwtProvider {

    /** user-service와 동일한 HMAC-SHA256 시크릿 문자열 */
    @Value("${jwt.secret}")
    private String secret;

    /** secret으로부터 생성된 HMAC-SHA 서명 키 */
    private SecretKey key;

    /** 시크릿 문자열로부터 서명 키를 초기화한다. */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 토큰의 유효성을 검증한다 (서명 + 만료 시간).
     *
     * @param token JWT 문자열
     * @return 유효하면 {@code true}, 만료·변조 등이면 {@code false}
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID를 추출한다.
     *
     * @param token JWT 문자열
     * @return subject에 저장된 사용자 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    /**
     * 토큰에서 이메일을 추출한다.
     *
     * @param token JWT 문자열
     * @return claim "email"에 저장된 이메일
     */
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /** 토큰을 파싱하여 Claims 객체를 반환한다. 서명 검증 및 만료 확인을 포함한다. */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
