package com.starlit.userservice.service;

import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.dto.WatchlistRequest;
import com.starlit.userservice.dto.WatchlistResponse;
import com.starlit.userservice.entity.Watchlist;
import com.starlit.userservice.repository.WatchlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @InjectMocks
    private WatchlistService watchlistService;

    @Mock
    private WatchlistRepository watchlistRepository;

    @Test
    @DisplayName("관심종목 추가 성공 - 저장되고 응답이 반환된다")
    void addWatchlist_success() {
        // given
        WatchlistRequest request = new WatchlistRequest("005930", "삼성전자");

        given(watchlistRepository.existsByUserIdAndStockCode(1L, "005930")).willReturn(false);
        given(watchlistRepository.save(any(Watchlist.class))).willAnswer(invocation -> {
            Watchlist w = invocation.getArgument(0);
            return Watchlist.builder()
                    .id(1L)
                    .userId(w.getUserId())
                    .stockCode(w.getStockCode())
                    .stockName(w.getStockName())
                    .createdAt(LocalDateTime.now())
                    .build();
        });

        // when
        WatchlistResponse response = watchlistService.addWatchlist(1L, request);

        // then
        assertThat(response.stockCode()).isEqualTo("005930");
        assertThat(response.stockName()).isEqualTo("삼성전자");
        verify(watchlistRepository).save(any(Watchlist.class));
    }

    @Test
    @DisplayName("관심종목 추가 실패 - 이미 등록된 종목이면 DUPLICATE_WATCHLIST 예외")
    void addWatchlist_duplicate() {
        // given
        WatchlistRequest request = new WatchlistRequest("005930", "삼성전자");
        given(watchlistRepository.existsByUserIdAndStockCode(1L, "005930")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> watchlistService.addWatchlist(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_WATCHLIST);
                });

        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("관심종목 목록 조회 - 해당 유저의 관심종목 리스트가 반환된다")
    void getWatchlist_success() {
        // given
        List<Watchlist> items = List.of(
                Watchlist.builder().id(1L).userId(1L).stockCode("005930").stockName("삼성전자")
                        .createdAt(LocalDateTime.now()).build(),
                Watchlist.builder().id(2L).userId(1L).stockCode("000660").stockName("SK하이닉스")
                        .createdAt(LocalDateTime.now()).build()
        );
        given(watchlistRepository.findByUserId(1L)).willReturn(items);

        // when
        List<WatchlistResponse> result = watchlistService.getWatchlist(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).stockCode()).isEqualTo("005930");
        assertThat(result.get(1).stockCode()).isEqualTo("000660");
    }

    @Test
    @DisplayName("관심종목 삭제 - deleteByUserIdAndStockCode가 호출된다")
    void deleteWatchlist_success() {
        // when
        watchlistService.deleteWatchlist(1L, "005930");

        // then
        verify(watchlistRepository).deleteByUserIdAndStockCode(1L, "005930");
    }
}
