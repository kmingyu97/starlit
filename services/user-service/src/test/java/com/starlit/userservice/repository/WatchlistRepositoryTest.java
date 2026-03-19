package com.starlit.userservice.repository;

import com.starlit.userservice.entity.Watchlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class WatchlistRepositoryTest {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @BeforeEach
    void setUp() {
        watchlistRepository.deleteAll();
    }

    @Test
    @DisplayName("관심종목을 저장하고 userId로 조회할 수 있다")
    void saveAndFindByUserId() {
        // given
        Watchlist item1 = Watchlist.builder()
                .userId(1L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();
        Watchlist item2 = Watchlist.builder()
                .userId(1L)
                .stockCode("000660")
                .stockName("SK하이닉스")
                .build();
        watchlistRepository.save(item1);
        watchlistRepository.save(item2);

        // when
        List<Watchlist> result = watchlistRepository.findByUserId(1L);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("다른 userId의 관심종목은 조회되지 않는다")
    void findByUserId_differentUser() {
        // given
        watchlistRepository.save(Watchlist.builder()
                .userId(1L).stockCode("005930").stockName("삼성전자").build());
        watchlistRepository.save(Watchlist.builder()
                .userId(2L).stockCode("000660").stockName("SK하이닉스").build());

        // when
        List<Watchlist> result = watchlistRepository.findByUserId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockCode()).isEqualTo("005930");
    }

    @Test
    @DisplayName("userId와 stockCode로 존재 여부를 확인할 수 있다")
    void existsByUserIdAndStockCode() {
        // given
        watchlistRepository.save(Watchlist.builder()
                .userId(1L).stockCode("005930").stockName("삼성전자").build());

        // when & then
        assertThat(watchlistRepository.existsByUserIdAndStockCode(1L, "005930")).isTrue();
        assertThat(watchlistRepository.existsByUserIdAndStockCode(1L, "000660")).isFalse();
        assertThat(watchlistRepository.existsByUserIdAndStockCode(2L, "005930")).isFalse();
    }

    @Test
    @DisplayName("같은 userId + stockCode 조합은 중복 저장할 수 없다")
    void uniqueConstraint_userIdAndStockCode() {
        // given
        watchlistRepository.save(Watchlist.builder()
                .userId(1L).stockCode("005930").stockName("삼성전자").build());

        Watchlist duplicate = Watchlist.builder()
                .userId(1L).stockCode("005930").stockName("삼성전자").build();

        // when & then
        assertThatThrownBy(() -> {
            watchlistRepository.save(duplicate);
            watchlistRepository.flush();
        });
    }

    @Test
    @DisplayName("저장된 관심종목의 createdAt이 자동으로 설정된다")
    void createdAtIsAutoSet() {
        // given
        Watchlist saved = watchlistRepository.save(Watchlist.builder()
                .userId(1L).stockCode("005930").stockName("삼성전자").build());

        // then
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
