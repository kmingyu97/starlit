package com.starlit.userservice.controller;

import tools.jackson.databind.ObjectMapper;
import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.dto.WatchlistRequest;
import com.starlit.userservice.dto.WatchlistResponse;
import com.starlit.userservice.service.WatchlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WatchlistService watchlistService;

    @Test
    @DisplayName("POST /api/users/watchlist - 관심종목 추가 성공 시 201 반환")
    void addWatchlist_success() throws Exception {
        // given
        WatchlistRequest request = new WatchlistRequest("005930", "삼성전자");
        WatchlistResponse response = new WatchlistResponse(1L, "005930", "삼성전자", LocalDateTime.now());

        given(watchlistService.addWatchlist(eq(1L), any(WatchlistRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/watchlist")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockCode").value("005930"))
                .andExpect(jsonPath("$.stockName").value("삼성전자"));
    }

    @Test
    @DisplayName("POST /api/users/watchlist - 중복 종목 시 409 반환")
    void addWatchlist_duplicate() throws Exception {
        // given
        WatchlistRequest request = new WatchlistRequest("005930", "삼성전자");

        given(watchlistService.addWatchlist(eq(1L), any(WatchlistRequest.class)))
                .willThrow(new CustomException(ErrorCode.DUPLICATE_WATCHLIST));

        // when & then
        mockMvc.perform(post("/api/users/watchlist")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_WATCHLIST"));
    }

    @Test
    @DisplayName("POST /api/users/watchlist - 종목코드 빈값이면 400 반환")
    void addWatchlist_blankStockCode() throws Exception {
        // given
        WatchlistRequest request = new WatchlistRequest("", "삼성전자");

        // when & then
        mockMvc.perform(post("/api/users/watchlist")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET /api/users/watchlist - 관심종목 목록 조회 성공 시 200 반환")
    void getWatchlist_success() throws Exception {
        // given
        List<WatchlistResponse> responses = List.of(
                new WatchlistResponse(1L, "005930", "삼성전자", LocalDateTime.now()),
                new WatchlistResponse(2L, "000660", "SK하이닉스", LocalDateTime.now())
        );

        given(watchlistService.getWatchlist(1L)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/users/watchlist")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].stockCode").value("005930"))
                .andExpect(jsonPath("$[1].stockCode").value("000660"));
    }

    @Test
    @DisplayName("DELETE /api/users/watchlist/{stockCode} - 관심종목 삭제 성공 시 204 반환")
    void deleteWatchlist_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/watchlist/005930")
                        .header("X-User-Id", "1"))
                .andExpect(status().isNoContent());

        verify(watchlistService).deleteWatchlist(1L, "005930");
    }

    @Test
    @DisplayName("GET /api/users/watchlist - X-User-Id 헤더 없으면 400 반환")
    void getWatchlist_missingHeader() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/watchlist"))
                .andExpect(status().isBadRequest());
    }
}
