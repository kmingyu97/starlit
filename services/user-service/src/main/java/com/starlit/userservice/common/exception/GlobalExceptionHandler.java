package com.starlit.userservice.common.exception;

import com.starlit.userservice.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기.
 *
 * <p>컨트롤러에서 발생하는 예외를 잡아 일관된 {@link ErrorResponse} 형식으로 반환한다.</p>
 *
 * <ul>
 *   <li>{@link CustomException} → ErrorCode 기반 응답</li>
 *   <li>{@link MethodArgumentNotValidException} → 유효성 검증 실패 응답 (첫 번째 필드 에러 메시지)</li>
 *   <li>{@link Exception} → 500 Internal Error (예상치 못한 예외)</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리.
     * ErrorCode에 정의된 HTTP 상태와 메시지로 응답한다.
     */
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

    /**
     * @Valid 유효성 검증 실패 처리.
     * DTO의 validation 어노테이션(@NotBlank, @Email 등)에서 발생한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // 첫 번째 필드 에러의 메시지를 사용
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

    /**
     * 요청 바인딩 예외 처리.
     * 필수 헤더, 쿠키, 파라미터 누락 시 발생한다.
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(ServletRequestBindingException ex) {
        log.warn("Request binding failed: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        ErrorCode.VALIDATION_ERROR.getHttpStatus().value(),
                        ErrorCode.VALIDATION_ERROR.name(),
                        ex.getMessage()
                ));
    }

    /**
     * 예상치 못한 예외 처리.
     * 위에서 잡히지 않은 모든 예외를 500으로 반환한다.
     */
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
