package com.starlit.stockservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * stock-service 에러 코드 정의.
 *
 * <p>각 에러 코드는 HTTP 상태 코드와 기본 메시지를 포함한다.
 * 클라이언트에 반환되는 에러 응답의 {@code code} 필드에는 enum 이름이 그대로 사용된다.</p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 - 입력값 검증 실패
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // 404 - 리소스 없음
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "종목을 찾을 수 없습니다."),

    // 500 - 서버 오류
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
