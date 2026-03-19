package com.starlit.userservice.dto;

import java.time.LocalDateTime;

public record WatchlistResponse(Long id, String stockCode, String stockName, LocalDateTime createdAt) {
}
