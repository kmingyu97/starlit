package com.starlit.userservice.controller;

import com.starlit.userservice.dto.WatchlistRequest;
import com.starlit.userservice.dto.WatchlistResponse;
import com.starlit.userservice.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistResponse addWatchlist(@RequestHeader("X-User-Id") Long userId,
                                          @Valid @RequestBody WatchlistRequest request) {
        return watchlistService.addWatchlist(userId, request);
    }

    @GetMapping
    public List<WatchlistResponse> getWatchlist(@RequestHeader("X-User-Id") Long userId) {
        return watchlistService.getWatchlist(userId);
    }

    @DeleteMapping("/{stockCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatchlist(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable String stockCode) {
        watchlistService.deleteWatchlist(userId, stockCode);
    }
}
