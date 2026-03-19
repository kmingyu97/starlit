package com.starlit.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WatchlistRequest(
        @NotBlank(message = "종목 코드는 필수입니다.")
        @Size(max = 20, message = "종목 코드는 20자 이내여야 합니다.")
        String stockCode,

        @NotBlank(message = "종목명은 필수입니다.")
        @Size(max = 100, message = "종목명은 100자 이내여야 합니다.")
        String stockName
) {
}
