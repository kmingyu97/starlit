package com.starlit.userservice.dto;

import java.time.LocalDateTime;

/**
 * 프로필 조회/수정 응답 DTO.
 *
 * @param id        사용자 ID
 * @param email     이메일
 * @param nickname  닉네임
 * @param createdAt 가입 일시
 */
public record ProfileResponse(Long id, String email, String nickname, LocalDateTime createdAt) {
}
