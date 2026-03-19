package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.StockDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 일별 시세 Repository.
 *
 * <p>종목별 시세 데이터를 날짜 기준으로 조회한다.</p>
 */
public interface StockDailyPriceRepository extends JpaRepository<StockDailyPrice, Long> {

    /** 특정 종목의 최근 시세를 조회한다 (거래일 내림차순). */
    List<StockDailyPrice> findByStockCodeOrderByTradeDateDesc(String stockCode);

    /** 특정 종목의 가장 최근 시세를 조회한다. */
    Optional<StockDailyPrice> findFirstByStockCodeOrderByTradeDateDesc(String stockCode);

    /** 특정 종목의 기간별 시세를 조회한다. */
    List<StockDailyPrice> findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(
            String stockCode, LocalDate startDate, LocalDate endDate);
}
