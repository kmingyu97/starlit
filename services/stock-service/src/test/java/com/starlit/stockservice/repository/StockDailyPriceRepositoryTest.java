package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.StockDailyPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockDailyPriceRepositoryTest {

    @Autowired
    private StockDailyPriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        priceRepository.save(StockDailyPrice.builder()
                .stockCode("005930").tradeDate(LocalDate.of(2026, 3, 17))
                .openPrice(71000).highPrice(72000).lowPrice(70500).closePrice(71500)
                .volume(10_000_000L).changeRate(new BigDecimal("1.42")).build());
        priceRepository.save(StockDailyPrice.builder()
                .stockCode("005930").tradeDate(LocalDate.of(2026, 3, 18))
                .openPrice(71500).highPrice(73000).lowPrice(71000).closePrice(72500)
                .volume(12_000_000L).changeRate(new BigDecimal("1.40")).build());
        priceRepository.save(StockDailyPrice.builder()
                .stockCode("005930").tradeDate(LocalDate.of(2026, 3, 19))
                .openPrice(72500).highPrice(73500).lowPrice(72000).closePrice(73000)
                .volume(11_000_000L).changeRate(new BigDecimal("0.69")).build());
    }

    @Test
    @DisplayName("최근 시세 순으로 조회한다")
    void findByStockCodeOrderByTradeDateDesc() {
        List<StockDailyPrice> prices = priceRepository.findByStockCodeOrderByTradeDateDesc("005930");

        assertThat(prices).hasSize(3);
        assertThat(prices.get(0).getTradeDate()).isEqualTo(LocalDate.of(2026, 3, 19));
    }

    @Test
    @DisplayName("가장 최근 시세 1건을 조회한다")
    void findFirstByStockCodeOrderByTradeDateDesc() {
        Optional<StockDailyPrice> latest = priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930");

        assertThat(latest).isPresent();
        assertThat(latest.get().getClosePrice()).isEqualTo(73000);
    }

    @Test
    @DisplayName("기간별 시세를 조회한다")
    void findByStockCodeAndTradeDateBetween() {
        List<StockDailyPrice> prices = priceRepository
                .findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(
                        "005930", LocalDate.of(2026, 3, 17), LocalDate.of(2026, 3, 18));

        assertThat(prices).hasSize(2);
        assertThat(prices.get(0).getTradeDate()).isEqualTo(LocalDate.of(2026, 3, 17));
    }

    @Test
    @DisplayName("존재하지 않는 종목 조회 시 빈 결과를 반환한다")
    void findByNonExistentStockCode() {
        Optional<StockDailyPrice> result = priceRepository.findFirstByStockCodeOrderByTradeDateDesc("999999");

        assertThat(result).isEmpty();
    }
}
