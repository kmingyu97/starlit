package com.starlit.communityservice.common.dto;

/**
 * 클라이언트에 반환되는 에러 응답 DTO.
 *
 * @param status  HTTP 상태 코드
 * @param code    에러 코드 (대문자 스네이크 케이스)
 * @param message 사용자에게 보여줄 메시지
 */
public record ErrorResponse(
        int status,
        String code,
        String message
) {
}
