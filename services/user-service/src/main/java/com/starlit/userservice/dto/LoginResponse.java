package com.starlit.userservice.dto;

/**
 * 로그인 응답 DTO.
 *
 * @param token    발급된 JWT 토큰 (클라이언트가 Authorization 헤더에 사용)
 * @param nickname 로그인한 사용자의 닉네임
 */
public record LoginResponse(String token, String nickname) {
}
