package com.starlit.userservice.common.dto;

/**
 * 클라이언트에 반환되는 에러 응답 DTO.
 *
 * <p>모든 서비스가 동일한 포맷을 사용한다.</p>
 *
 * <pre>
 * {
 *   "status": 400,
 *   "code": "VALIDATION_ERROR",
 *   "message": "이메일 형식이 올바르지 않습니다."
 * }
 * </pre>
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
