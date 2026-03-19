package com.starlit.stockservice.common.exception;

import com.starlit.stockservice.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기.
 *
 * <p>컨트롤러에서 발생하는 예외를 잡아 일관된 {@link ErrorResponse} 형식으로 반환한다.</p>
 *
 * <ul>
 *   <li>{@link CustomException} → ErrorCode 기반 응답</li>
 *   <li>{@link MethodArgumentNotValidException} → 유효성 검증 실패 응답</li>
 *   <li>{@link Exception} → 500 Internal Error</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 비즈니스 로직 예외 처리. */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("CustomException: {} - {}", errorCode.name(), ex.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponse(
                        errorCode.getHttpStatus().value(),
                        errorCode.name(),
                        ex.getMessage()
                ));
    }

    /** @Valid 유효성 검증 실패 처리. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        log.warn("Validation failed: {}", message);

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        ErrorCode.VALIDATION_ERROR.getHttpStatus().value(),
                        ErrorCode.VALIDATION_ERROR.name(),
                        message
                ));
    }

    /** 예상치 못한 예외 처리. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected exception", ex);

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(
                        ErrorCode.INTERNAL_ERROR.getHttpStatus().value(),
                        ErrorCode.INTERNAL_ERROR.name(),
                        ErrorCode.INTERNAL_ERROR.getMessage()
                ));
    }
}
