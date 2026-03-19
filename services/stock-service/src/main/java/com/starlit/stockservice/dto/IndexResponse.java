package com.starlit.stockservice.dto;

import java.math.BigDecimal;

/**
 * 주요 지수 응답 DTO.
 *
 * @param name       지수명 (코스피, 코스닥 등)
 * @param value      현재 지수
 * @param change     전일 대비 변동
 * @param changeRate 등락률(%)
 */
public record IndexResponse(
        String name,
        BigDecimal value,
        BigDecimal change,
        BigDecimal changeRate
) {
}
