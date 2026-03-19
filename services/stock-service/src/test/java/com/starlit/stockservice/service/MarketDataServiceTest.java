package com.starlit.stockservice.service;

import com.starlit.stockservice.dto.ExchangeRateResponse;
import com.starlit.stockservice.dto.IndexResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarketDataServiceTest {

    private final MarketDataService marketDataService = new MarketDataService();

    @Test
    @DisplayName("주요 지수 4개를 반환한다")
    void getIndices() {
        List<IndexResponse> indices = marketDataService.getIndices();

        assertThat(indices).hasSize(4);
        assertThat(indices).extracting(IndexResponse::name)
                .containsExactly("코스피", "코스닥", "다우존스", "나스닥");
    }

    @Test
    @DisplayName("주요 환율 3개를 반환한다")
    void getExchangeRates() {
        List<ExchangeRateResponse> rates = marketDataService.getExchangeRates();

        assertThat(rates).hasSize(3);
        assertThat(rates).extracting(ExchangeRateResponse::currency)
                .containsExactly("USD", "JPY", "EUR");
    }
}
