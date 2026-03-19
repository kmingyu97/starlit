package com.starlit.stockservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 일별 시세 엔티티.
 *
 * <p>종목별 일별 OHLCV(시가·고가·저가·종가·거래량) 데이터를 저장한다.
 * 외부 API로부터 조회한 시세를 캐시하는 용도로 사용된다.</p>
 */
@Entity
@Table(name = "stock_daily_price",
        uniqueConstraints = @UniqueConstraint(columnNames = {"stock_code", "trade_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 종목 코드 */
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    /** 거래일 */
    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    /** 시가 */
    @Column(name = "open_price")
    private Integer openPrice;

    /** 고가 */
    @Column(name = "high_price")
    private Integer highPrice;

    /** 저가 */
    @Column(name = "low_price")
    private Integer lowPrice;

    /** 종가 */
    @Column(name = "close_price")
    private Integer closePrice;

    /** 거래량 */
    private Long volume;

    /** 등락률(%) */
    @Column(name = "change_rate", precision = 6, scale = 2)
    private BigDecimal changeRate;

    @Builder
    public StockDailyPrice(String stockCode, LocalDate tradeDate, Integer openPrice,
                           Integer highPrice, Integer lowPrice, Integer closePrice,
                           Long volume, BigDecimal changeRate) {
        this.stockCode = stockCode;
        this.tradeDate = tradeDate;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.changeRate = changeRate;
    }
}
