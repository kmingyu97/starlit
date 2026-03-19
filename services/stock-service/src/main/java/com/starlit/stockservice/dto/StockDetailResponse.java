package com.starlit.stockservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 종목 상세 응답 DTO.
 *
 * <p>종목 기본 정보와 최근 시세 상세(OHLCV)를 포함한다.</p>
 *
 * @param stockCode  종목 코드
 * @param stockName  종목명
 * @param market     시장 구분
 * @param sector     업종
 * @param marketCap  시가총액
 * @param tradeDate  최근 거래일
 * @param openPrice  시가
 * @param highPrice  고가
 * @param lowPrice   저가
 * @param closePrice 종가
 * @param volume     거래량
 * @param changeRate 등락률(%)
 */
public record StockDetailResponse(
        String stockCode,
        String stockName,
        String market,
        String sector,
        Long marketCap,
        LocalDate tradeDate,
        Integer openPrice,
        Integer highPrice,
        Integer lowPrice,
        Integer closePrice,
        Long volume,
        BigDecimal changeRate
) {
}
