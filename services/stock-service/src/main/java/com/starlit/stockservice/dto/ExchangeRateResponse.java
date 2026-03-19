package com.starlit.stockservice.dto;

import java.math.BigDecimal;

/**
 * 환율 응답 DTO.
 *
 * @param currency   통화 코드 (USD, JPY, EUR)
 * @param rate       환율 (원화 기준)
 * @param change     전일 대비 변동
 * @param changeRate 등락률(%)
 */
public record ExchangeRateResponse(
        String currency,
        BigDecimal rate,
        BigDecimal change,
        BigDecimal changeRate
) {
}
