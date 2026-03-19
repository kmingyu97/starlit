package com.starlit.stockservice.dto;

import java.math.BigDecimal;

/**
 * 종목 목록 응답 DTO.
 *
 * <p>종목 기본 정보와 최근 시세 요약을 포함한다.</p>
 *
 * @param stockCode  종목 코드
 * @param stockName  종목명
 * @param market     시장 구분 (KOSPI/KOSDAQ)
 * @param sector     업종
 * @param closePrice 최근 종가
 * @param changeRate 등락률(%)
 * @param marketCap  시가총액
 */
public record StockResponse(
        String stockCode,
        String stockName,
        String market,
        String sector,
        Integer closePrice,
        BigDecimal changeRate,
        Long marketCap
) {
}
