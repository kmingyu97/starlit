package com.starlit.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 관심종목 추가 요청 DTO.
 *
 * @param stockCode 종목 코드 (예: "005930")
 * @param stockName 종목명 (예: "삼성전자")
 */
public record WatchlistRequest(
        @NotBlank(message = "종목 코드는 필수입니다.")
        @Size(max = 20, message = "종목 코드는 20자 이내여야 합니다.")
        String stockCode,

        @NotBlank(message = "종목명은 필수입니다.")
        @Size(max = 100, message = "종목명은 100자 이내여야 합니다.")
        String stockName
) {
}
