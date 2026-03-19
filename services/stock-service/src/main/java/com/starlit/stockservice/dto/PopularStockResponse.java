package com.starlit.stockservice.dto;

/**
 * 인기 검색 종목 응답 DTO.
 *
 * @param rank       순위
 * @param stockCode  종목 코드
 * @param stockName  종목명
 * @param searchCount 검색 횟수
 */
public record PopularStockResponse(
        int rank,
        String stockCode,
        String stockName,
        long searchCount
) {
}
