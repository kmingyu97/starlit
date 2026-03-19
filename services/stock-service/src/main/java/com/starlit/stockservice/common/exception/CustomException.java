package com.starlit.stockservice.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 커스텀 예외.
 *
 * <p>{@link ErrorCode}를 기반으로 HTTP 상태 코드와 메시지를 결정한다.</p>
 *
 * <pre>
 * throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
 * throw new CustomException(ErrorCode.STOCK_NOT_FOUND, "삼성전자(005930)를 찾을 수 없습니다.");
 * </pre>
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
