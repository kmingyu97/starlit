package com.starlit.stockservice.controller;

import com.starlit.stockservice.dto.ExchangeRateResponse;
import com.starlit.stockservice.dto.IndexResponse;
import com.starlit.stockservice.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 시장 데이터 컨트롤러 (지수·환율).
 *
 * <p>비회원도 접근 가능한 공개 API이다.</p>
 *
 * <pre>
 * GET /api/stocks/indices        → 주요 지수
 * GET /api/stocks/exchange-rates → 환율
 * </pre>
 */
@RestController
@RequestMapping("/api/stocks")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /** 주요 지수(코스피, 코스닥, 다우, 나스닥)를 조회한다. */
    @GetMapping("/indices")
    public ResponseEntity<List<IndexResponse>> getIndices() {
        return ResponseEntity.ok(marketDataService.getIndices());
    }

    /** 주요 환율(USD, JPY, EUR)을 조회한다. */
    @GetMapping("/exchange-rates")
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        return ResponseEntity.ok(marketDataService.getExchangeRates());
    }
}
