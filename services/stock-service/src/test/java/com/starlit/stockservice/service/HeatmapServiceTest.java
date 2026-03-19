package com.starlit.stockservice.service;

import com.starlit.stockservice.dto.HeatmapResponse;
import com.starlit.stockservice.entity.StockDailyPrice;
import com.starlit.stockservice.entity.StockMaster;
import com.starlit.stockservice.repository.StockDailyPriceRepository;
import com.starlit.stockservice.repository.StockMasterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HeatmapServiceTest {

    @InjectMocks
    private HeatmapService heatmapService;

    @Mock
    private StockMasterRepository stockMasterRepository;

    @Mock
    private StockDailyPriceRepository priceRepository;

    @Test
    @DisplayName("섹터별 히트맵 데이터를 반환한다")
    void getHeatmap() {
        StockMaster samsung = StockMaster.builder()
                .stockCode("005930").stockName("삼성전자").market("KOSPI")
                .sector("반도체").marketCap(400_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build();
        StockDailyPrice price = StockDailyPrice.builder()
                .stockCode("005930").tradeDate(LocalDate.now())
                .closePrice(72500).changeRate(new BigDecimal("1.40")).build();

        given(stockMasterRepository.findDistinctSectors()).willReturn(List.of("반도체"));
        given(stockMasterRepository.findBySector("반도체")).willReturn(List.of(samsung));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("005930"))
                .willReturn(Optional.of(price));

        List<HeatmapResponse> result = heatmapService.getHeatmap();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sector()).isEqualTo("반도체");
        assertThat(result.get(0).stocks()).hasSize(1);
        assertThat(result.get(0).stocks().get(0).changeRate()).isEqualByComparingTo("1.40");
    }

    @Test
    @DisplayName("시세 없는 종목도 히트맵에 포함된다")
    void getHeatmap_noPriceData() {
        StockMaster stock = StockMaster.builder()
                .stockCode("999999").stockName("테스트종목").market("KOSPI")
                .sector("기타").marketCap(1_000_000_000L)
                .updatedAt(LocalDateTime.now()).build();

        given(stockMasterRepository.findDistinctSectors()).willReturn(List.of("기타"));
        given(stockMasterRepository.findBySector("기타")).willReturn(List.of(stock));
        given(priceRepository.findFirstByStockCodeOrderByTradeDateDesc("999999"))
                .willReturn(Optional.empty());

        List<HeatmapResponse> result = heatmapService.getHeatmap();

        assertThat(result.get(0).stocks().get(0).changeRate()).isNull();
    }
}
