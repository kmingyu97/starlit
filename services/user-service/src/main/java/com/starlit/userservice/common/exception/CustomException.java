package com.starlit.userservice.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 커스텀 예외.
 *
 * <p>{@link ErrorCode}를 기반으로 HTTP 상태 코드와 메시지를 결정한다.
 * 기본 메시지 대신 커스텀 메시지를 전달할 수도 있다.</p>
 *
 * <pre>
 * // ErrorCode의 기본 메시지 사용
 * throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
 *
 * // 커스텀 메시지 사용
 * throw new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
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
