package com.starlit.userservice.service;

import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.dto.WatchlistRequest;
import com.starlit.userservice.dto.WatchlistResponse;
import com.starlit.userservice.entity.Watchlist;
import com.starlit.userservice.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관심종목 비즈니스 로직.
 *
 * <p>사용자별 관심종목 추가·조회·삭제를 처리한다.
 * 동일 사용자가 같은 종목을 중복 등록할 수 없다.</p>
 */
@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    /**
     * 관심종목을 추가한다.
     *
     * @param userId  사용자 ID (X-User-Id 헤더)
     * @param request 종목 코드·종목명
     * @return 저장된 관심종목 정보
     * @throws CustomException 이미 등록된 종목이면 DUPLICATE_WATCHLIST
     */
    @Transactional
    public WatchlistResponse addWatchlist(Long userId, WatchlistRequest request) {
        if (watchlistRepository.existsByUserIdAndStockCode(userId, request.stockCode())) {
            throw new CustomException(ErrorCode.DUPLICATE_WATCHLIST);
        }

        Watchlist watchlist = Watchlist.builder()
                .userId(userId)
                .stockCode(request.stockCode())
                .stockName(request.stockName())
                .build();

        Watchlist saved = watchlistRepository.save(watchlist);

        return new WatchlistResponse(saved.getId(), saved.getStockCode(), saved.getStockName(), saved.getCreatedAt());
    }

    /**
     * 사용자의 관심종목 목록을 조회한다.
     *
     * @param userId 사용자 ID (X-User-Id 헤더)
     * @return 관심종목 리스트 (없으면 빈 리스트)
     */
    @Transactional(readOnly = true)
    public List<WatchlistResponse> getWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId).stream()
                .map(w -> new WatchlistResponse(w.getId(), w.getStockCode(), w.getStockName(), w.getCreatedAt()))
                .toList();
    }

    /**
     * 관심종목을 삭제한다.
     *
     * <p>존재하지 않는 종목을 삭제해도 예외가 발생하지 않는다 (멱등성).</p>
     *
     * @param userId    사용자 ID (X-User-Id 헤더)
     * @param stockCode 삭제할 종목 코드
     */
    @Transactional
    public void deleteWatchlist(Long userId, String stockCode) {
        watchlistRepository.deleteByUserIdAndStockCode(userId, stockCode);
    }
}
