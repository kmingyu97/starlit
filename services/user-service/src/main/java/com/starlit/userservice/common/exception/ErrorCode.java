package com.starlit.userservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전역 에러 코드 정의.
 *
 * <p>각 에러 코드는 HTTP 상태 코드와 기본 메시지를 포함한다.
 * 클라이언트에 반환되는 에러 응답의 {@code code} 필드에는 enum 이름이 그대로 사용된다.
 * (예: DUPLICATE_EMAIL → "DUPLICATE_EMAIL")</p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 - 입력값 검증 실패
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // 401 - 인증 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    // 403 - 권한 없음
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 404 - 리소스 없음
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 409 - 중복
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    DUPLICATE_WATCHLIST(HttpStatus.CONFLICT, "이미 등록된 관심종목입니다."),

    // 500 - 서버 오류
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
