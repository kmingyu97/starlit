package com.starlit.userservice.common;

import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.common.exception.GlobalExceptionHandler;
import com.starlit.userservice.common.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandler 단위 테스트.
 *
 * 각 예외 타입별로 올바른 ErrorResponse가 반환되는지 검증한다.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("CustomException 발생 시 ErrorCode에 맞는 응답을 반환한다")
    void handleCustomException() {
        // given
        CustomException ex = new CustomException(ErrorCode.DUPLICATE_EMAIL);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleCustomException(ex);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE_EMAIL");
        assertThat(response.getBody().message()).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("CustomException에 커스텀 메시지를 전달하면 해당 메시지가 반환된다")
    void handleCustomExceptionWithCustomMessage() {
        // given
        CustomException ex = new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleCustomException(ex);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("예상치 못한 예외 발생 시 500 Internal Error를 반환한다")
    void handleUnexpectedException() {
        // given
        Exception ex = new RuntimeException("DB connection failed");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("서버 내부 오류가 발생했습니다.");
    }
}
