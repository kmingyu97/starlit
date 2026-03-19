package com.starlit.stockservice.controller;

import com.starlit.stockservice.dto.HeatmapResponse;
import com.starlit.stockservice.service.HeatmapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 히트맵 컨트롤러.
 *
 * <p>섹터별 종목 등락률 데이터를 제공한다. 공개 API이다.</p>
 *
 * <pre>
 * GET /api/stocks/heatmap → 섹터별 히트맵 데이터
 * </pre>
 */
@RestController
@RequestMapping("/api/stocks")
public class HeatmapController {

    private final HeatmapService heatmapService;

    public HeatmapController(HeatmapService heatmapService) {
        this.heatmapService = heatmapService;
    }

    /** 섹터별 히트맵 데이터를 조회한다. */
    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapResponse>> getHeatmap() {
        return ResponseEntity.ok(heatmapService.getHeatmap());
    }
}
