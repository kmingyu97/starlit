package com.starlit.userservice.controller;

import com.starlit.userservice.dto.WatchlistRequest;
import com.starlit.userservice.dto.WatchlistResponse;
import com.starlit.userservice.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관심종목 REST API 컨트롤러.
 *
 * <p>모든 엔드포인트는 Gateway가 JWT에서 추출한 {@code X-User-Id} 헤더를 필요로 한다.</p>
 *
 * <pre>
 * POST   /api/users/watchlist              → 관심종목 추가 (201)
 * GET    /api/users/watchlist              → 관심종목 목록 조회 (200)
 * DELETE /api/users/watchlist/{stockCode}  → 관심종목 삭제 (204)
 * </pre>
 */
@RestController
@RequestMapping("/api/users/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    /** 관심종목 추가. 이미 등록된 종목이면 409. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistResponse addWatchlist(@RequestHeader("X-User-Id") Long userId,
                                          @Valid @RequestBody WatchlistRequest request) {
        return watchlistService.addWatchlist(userId, request);
    }

    /** 관심종목 목록 조회. 등록된 종목이 없으면 빈 배열 반환. */
    @GetMapping
    public List<WatchlistResponse> getWatchlist(@RequestHeader("X-User-Id") Long userId) {
        return watchlistService.getWatchlist(userId);
    }

    /** 관심종목 삭제. 존재하지 않아도 204 반환 (멱등). */
    @DeleteMapping("/{stockCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatchlist(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable String stockCode) {
        watchlistService.deleteWatchlist(userId, stockCode);
    }
}
