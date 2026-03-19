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

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

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

    @Transactional(readOnly = true)
    public List<WatchlistResponse> getWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId).stream()
                .map(w -> new WatchlistResponse(w.getId(), w.getStockCode(), w.getStockName(), w.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void deleteWatchlist(Long userId, String stockCode) {
        watchlistRepository.deleteByUserIdAndStockCode(userId, stockCode);
    }
}
