package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.StockMaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockMasterRepositoryTest {

    @Autowired
    private StockMasterRepository stockMasterRepository;

    @BeforeEach
    void setUp() {
        stockMasterRepository.save(StockMaster.builder()
                .stockCode("005930").stockName("삼성전자").market("KOSPI")
                .sector("반도체").marketCap(400_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build());
        stockMasterRepository.save(StockMaster.builder()
                .stockCode("000660").stockName("SK하이닉스").market("KOSPI")
                .sector("반도체").marketCap(100_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build());
        stockMasterRepository.save(StockMaster.builder()
                .stockCode("035720").stockName("카카오").market("KOSPI")
                .sector("소프트웨어").marketCap(20_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build());
        stockMasterRepository.save(StockMaster.builder()
                .stockCode("247540").stockName("에코프로비엠").market("KOSDAQ")
                .sector("2차전지").marketCap(15_000_000_000_000L)
                .updatedAt(LocalDateTime.now()).build());
    }

    @Test
    @DisplayName("종목명으로 검색한다")
    void searchByKeyword_name() {
        Page<StockMaster> result = stockMasterRepository.searchByKeyword("삼성", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockCode()).isEqualTo("005930");
    }

    @Test
    @DisplayName("종목코드로 검색한다")
    void searchByKeyword_code() {
        Page<StockMaster> result = stockMasterRepository.searchByKeyword("0059", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockName()).isEqualTo("삼성전자");
    }

    @Test
    @DisplayName("시장별 종목을 조회한다")
    void findByMarket() {
        Page<StockMaster> kospi = stockMasterRepository.findByMarket("KOSPI", PageRequest.of(0, 10));
        Page<StockMaster> kosdaq = stockMasterRepository.findByMarket("KOSDAQ", PageRequest.of(0, 10));

        assertThat(kospi.getContent()).hasSize(3);
        assertThat(kosdaq.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("섹터별 종목을 조회한다")
    void findBySector() {
        List<StockMaster> semiconductors = stockMasterRepository.findBySector("반도체");

        assertThat(semiconductors).hasSize(2);
    }

    @Test
    @DisplayName("전체 섹터 목록을 조회한다")
    void findDistinctSectors() {
        List<String> sectors = stockMasterRepository.findDistinctSectors();

        assertThat(sectors).containsExactly("2차전지", "반도체", "소프트웨어");
    }
}
