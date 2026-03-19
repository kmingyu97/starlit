package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.SearchLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SearchLogRepositoryTest {

    @Autowired
    private SearchLogRepository searchLogRepository;

    @Test
    @DisplayName("인기 검색 종목을 검색 횟수 내림차순으로 조회한다")
    void findPopularStockCodes() {
        searchLogRepository.save(new SearchLog("005930"));
        searchLogRepository.save(new SearchLog("005930"));
        searchLogRepository.save(new SearchLog("005930"));
        searchLogRepository.save(new SearchLog("000660"));
        searchLogRepository.save(new SearchLog("000660"));
        searchLogRepository.save(new SearchLog("035720"));

        List<Object[]> popular = searchLogRepository.findPopularStockCodes();

        assertThat(popular).hasSize(3);
        assertThat(popular.get(0)[0]).isEqualTo("005930");
        assertThat((Long) popular.get(0)[1]).isEqualTo(3L);
        assertThat(popular.get(1)[0]).isEqualTo("000660");
    }
}
