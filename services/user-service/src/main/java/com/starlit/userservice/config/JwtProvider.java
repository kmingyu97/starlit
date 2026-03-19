package com.starlit.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 발급·검증 유틸리티.
 *
 * <p>HS256(HMAC-SHA256) 알고리즘을 사용하며, {@code jwt.secret}과 {@code jwt.expiration}
 * 프로퍼티로 시크릿 키와 만료 시간(ms)을 설정한다.</p>
 *
 * <p>토큰 구조:</p>
 * <ul>
 *   <li>subject: userId (Long → String)</li>
 *   <li>claim "email": 사용자 이메일</li>
 *   <li>issuedAt / expiration: 발급·만료 시각</li>
 * </ul>
 *
 * <p>user-service에서 발급하고, gateway에서 동일한 시크릿 키로 검증한다.</p>
 */
@Component
public class JwtProvider {

    /** HMAC-SHA256 서명에 사용할 시크릿 문자열 */
    @Value("${jwt.secret}")
    private String secret;

    /** 토큰 만료 시간 (밀리초). 기본값 86400000 = 24시간 */
    @Value("${jwt.expiration}")
    private long expiration;

    /** secret으로부터 생성된 HMAC-SHA 서명 키 */
    private SecretKey key;

    /** 시크릿 문자열로부터 서명 키를 초기화한다. */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰을 생성한다.
     *
     * @param userId 토큰 subject에 저장할 사용자 ID
     * @param email  토큰 claim에 저장할 사용자 이메일
     * @return 서명된 JWT 문자열
     */
    public String createToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID를 추출한다.
     *
     * @param token JWT 문자열
     * @return subject에 저장된 사용자 ID
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰의 유효성을 검증한다.
     *
     * <p>서명 검증과 만료 시간을 확인한다.</p>
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

    /** 토큰을 파싱하여 Claims 객체를 반환한다. 서명 검증 및 만료 확인을 포함한다. */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
