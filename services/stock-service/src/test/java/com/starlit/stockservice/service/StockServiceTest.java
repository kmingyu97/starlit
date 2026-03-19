package com.starlit.stockservice.service;

import com.starlit.stockservice.common.exception.CustomException;
import com.starlit.stockservice.dto.PopularStockResponse;
import com.starlit.stockservice.dto.StockDetailResponse;
import com.starlit.stockservice.dto.StockResponse;
import com.starlit.stockservice.entity.SearchLog;
import com.starlit.stockservice.entity.StockDailyPrice;
import com.starlit.stockservice.entity.StockMaster;
import com.starlit.stockservice.repository.SearchLogRepository;
import com.starlit.stockservice.repository.StockDailyPriceRepository;
import com.starlit.stockservice.repository.StockMasterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockMasterRepository stockMasterRepository;

    @Mock
    private StockDailyPriceRepository priceRepository;

    @Mock
    private SearchLogRepository searchLogRepository;

    private StockMaster createSamsungMaster() {
        return StockMaster.builder()
                .stockCode("005930").stockName("삼성전자").market("KOSPI")
                .sector("반도체").marketCap(400_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build();
    }

    private StockDailyPrice createSamsungPrice() {
        return StockDailyPrice.builder()
                .stockCode("005930").tradeDate(LocalDate.of(2026, 3, 19))
                .openPrice(71000).highPrice(73000).lowPrice(70500).closePrice(72500)
                .volume(12_000_000L).changeRate(new BigDecimal("1.40")).build();
    }

    @Test
    @DisplayName("키워드 없이 전체 종목 목록을 조회한다")
    void getStocks_all() {
        Pageable pageable = PageRequest.of(0, 20);
        StockMaster samsung = createSamsungMaster();
        given(stockMasterRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(samsung)));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930"))
                .willReturn(Optional.of(createSamsungPrice()));

        Page<StockResponse> result = stockService.getStocks(null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).stockName()).isEqualTo("삼성전자");
        assertThat(result.getContent().get(0).closePrice()).isEqualTo(72500);
    }

    @Test
    @DisplayName("키워드로 종목을 검색한다")
    void getStocks_withKeyword() {
        Pageable pageable = PageRequest.of(0, 20);
        StockMaster samsung = createSamsungMaster();
        given(stockMasterRepository.searchByKeyword("삼성", pageable))
                .willReturn(new PageImpl<>(List.of(samsung)));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930"))
                .willReturn(Optional.of(createSamsungPrice()));

        Page<StockResponse> result = stockService.getStocks("삼성", null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("시장별 종목을 조회한다")
    void getStocks_byMarket() {
        Pageable pageable = PageRequest.of(0, 20);
        StockMaster samsung = createSamsungMaster();
        given(stockMasterRepository.findByMarket("KOSPI", pageable))
                .willReturn(new PageImpl<>(List.of(samsung)));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930"))
                .willReturn(Optional.of(createSamsungPrice()));

        Page<StockResponse> result = stockService.getStocks(null, "KOSPI", pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("종목 상세를 조회하면 검색 로그가 기록된다")
    void getStockDetail_success() {
        StockMaster samsung = createSamsungMaster();
        StockDailyPrice price = createSamsungPrice();
        given(stockMasterRepository.findById("005930")).willReturn(Optional.of(samsung));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930"))
                .willReturn(Optional.of(price));

        StockDetailResponse result = stockService.getStockDetail("005930");

        assertThat(result.stockName()).isEqualTo("삼성전자");
        assertThat(result.closePrice()).isEqualTo(72500);
        verify(searchLogRepository).save(any(SearchLog.class));
    }

    @Test
    @DisplayName("존재하지 않는 종목 조회 시 예외가 발생한다")
    void getStockDetail_notFound() {
        given(stockMasterRepository.findById("999999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getStockDetail("999999"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("인기 검색 종목 TOP 10을 조회한다")
    void getPopularStocks() {
        List<Object[]> popular = List.of(
                new Object[]{"005930", 10L},
                new Object[]{"000660", 5L}
        );
        given(searchLogRepository.findPopularStockCodes()).willReturn(popular);
        given(stockMasterRepository.findById("005930"))
                .willReturn(Optional.of(createSamsungMaster()));
        given(stockMasterRepository.findById("000660"))
                .willReturn(Optional.of(StockMaster.builder()
                        .stockCode("000660").stockName("SK하이닉스").market("KOSPI")
                        .sector("반도체").build()));

        List<PopularStockResponse> result = stockService.getPopularStocks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).stockName()).isEqualTo("삼성전자");
        assertThat(result.get(0).searchCount()).isEqualTo(10);
        assertThat(result.get(1).rank()).isEqualTo(2);
    }
}
