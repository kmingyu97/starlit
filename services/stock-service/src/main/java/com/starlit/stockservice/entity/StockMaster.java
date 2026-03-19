package com.starlit.stockservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 종목 마스터 엔티티.
 *
 * <p>KRX에서 동기화한 종목 기본 정보를 저장한다.
 * 종목 코드({@code stockCode})가 PK이며, 시장 구분과 섹터 정보를 포함한다.</p>
 */
@Entity
@Table(name = "stock_master")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMaster {

    /** 종목 코드 (예: 005930) */
    @Id
    @Column(name = "stock_code", length = 20)
    private String stockCode;

    /** 종목명 (예: 삼성전자) */
    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    /** 시장 구분 (KOSPI / KOSDAQ) */
    @Column(nullable = false, length = 10)
    private String market;

    /** 업종 (예: 반도체) */
    @Column(length = 100)
    private String sector;

    /** 시가총액 */
    @Column(name = "market_cap")
    private Long marketCap;

    /** 최종 갱신 시각 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public StockMaster(String stockCode, String stockName, String market, String sector,
                       Long marketCap, LocalDateTime updatedAt) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.market = market;
        this.sector = sector;
        this.marketCap = marketCap;
        this.updatedAt = updatedAt;
    }
}
