package com.starlit.userservice.dto;

import java.time.LocalDateTime;

/**
 * 관심종목 응답 DTO.
 *
 * @param id        관심종목 ID
 * @param stockCode 종목 코드
 * @param stockName 종목명
 * @param createdAt 등록 일시
 */
public record WatchlistResponse(Long id, String stockCode, String stockName, LocalDateTime createdAt) {
}
