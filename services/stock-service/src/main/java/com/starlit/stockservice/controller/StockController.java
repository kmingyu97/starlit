package com.starlit.stockservice.controller;

import com.starlit.stockservice.dto.PopularStockResponse;
import com.starlit.stockservice.dto.StockDetailResponse;
import com.starlit.stockservice.dto.StockResponse;
import com.starlit.stockservice.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 종목 조회 컨트롤러.
 *
 * <p>모든 API는 공개(비회원 접근 가능)이며, JWT 인증이 필요하지 않다.</p>
 *
 * <pre>
 * GET /api/stocks              → 종목 목록 (검색, 페이징)
 * GET /api/stocks/popular      → 인기 검색 TOP 10
 * GET /api/stocks/{stockCode}  → 종목 상세
 * </pre>
 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * 종목 목록을 조회한다.
     *
     * @param keyword 검색 키워드 (종목명 또는 코드)
     * @param market  시장 필터 (KOSPI/KOSDAQ)
     * @param pageable 페이징 정보 (기본 20건)
     * @return 종목 목록
     */
    @GetMapping
    public ResponseEntity<Page<StockResponse>> getStocks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String market,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(stockService.getStocks(keyword, market, pageable));
    }

    /**
     * 인기 검색 종목 TOP 10을 조회한다.
     *
     * @return 인기 검색 종목 목록
     */
    @GetMapping("/popular")
    public ResponseEntity<List<PopularStockResponse>> getPopularStocks() {
        return ResponseEntity.ok(stockService.getPopularStocks());
    }

    /**
     * 종목 상세 정보를 조회한다.
     *
     * @param stockCode 종목 코드
     * @return 종목 상세 (기본 정보 + 최근 시세)
     */
    @GetMapping("/{stockCode}")
    public ResponseEntity<StockDetailResponse> getStockDetail(@PathVariable String stockCode) {
        return ResponseEntity.ok(stockService.getStockDetail(stockCode));
    }
}
