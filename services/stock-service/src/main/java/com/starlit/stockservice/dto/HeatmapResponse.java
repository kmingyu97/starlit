package com.starlit.stockservice.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 히트맵 응답 DTO.
 *
 * <p>섹터별 종목 등락률과 시가총액 데이터를 포함한다.</p>
 *
 * @param sector 섹터명
 * @param stocks 섹터에 속한 종목 목록
 */
public record HeatmapResponse(
        String sector,
        List<HeatmapStock> stocks
) {
    /**
     * 히트맵에 표시할 개별 종목 정보.
     *
     * @param stockCode  종목 코드
     * @param stockName  종목명
     * @param changeRate 등락률(%)
     * @param marketCap  시가총액
     */
    public record HeatmapStock(
            String stockCode,
            String stockName,
            BigDecimal changeRate,
            Long marketCap
    ) {
    }
}
