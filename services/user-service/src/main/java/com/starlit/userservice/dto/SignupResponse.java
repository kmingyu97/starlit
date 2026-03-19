package com.starlit.userservice.dto;

/**
 * 회원가입 응답 DTO.
 *
 * @param id       생성된 사용자 ID
 * @param email    등록된 이메일
 * @param nickname 등록된 닉네임
 */
public record SignupResponse(
        Long id,
        String email,
        String nickname
) {
}
