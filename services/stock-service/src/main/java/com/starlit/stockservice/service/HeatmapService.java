package com.starlit.stockservice.service;

import com.starlit.stockservice.dto.HeatmapResponse;
import com.starlit.stockservice.entity.StockMaster;
import com.starlit.stockservice.repository.StockDailyPriceRepository;
import com.starlit.stockservice.repository.StockMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 히트맵 서비스.
 *
 * <p>섹터별 종목 등락률을 집계하여 히트맵 데이터를 생성한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class HeatmapService {

    private final StockMasterRepository stockMasterRepository;
    private final StockDailyPriceRepository priceRepository;

    public HeatmapService(StockMasterRepository stockMasterRepository,
                          StockDailyPriceRepository priceRepository) {
        this.stockMasterRepository = stockMasterRepository;
        this.priceRepository = priceRepository;
    }

    /**
     * 섹터별 히트맵 데이터를 조회한다.
     *
     * @return 섹터별 종목 목록 (등락률, 시가총액 포함)
     */
    public List<HeatmapResponse> getHeatmap() {
        List<String> sectors = stockMasterRepository.findDistinctSectors();

        return sectors.stream()
                .map(sector -> {
                    List<StockMaster> stocks = stockMasterRepository.findBySector(sector);
                    List<HeatmapResponse.HeatmapStock> heatmapStocks = stocks.stream()
                            .map(stock -> priceRepository
                                    .findFirstByStockCodeOrderByTradeDateDesc(stock.getStockCode())
                                    .map(price -> new HeatmapResponse.HeatmapStock(
                                            stock.getStockCode(), stock.getStockName(),
                                            price.getChangeRate(), stock.getMarketCap()))
                                    .orElse(new HeatmapResponse.HeatmapStock(
                                            stock.getStockCode(), stock.getStockName(),
                                            null, stock.getMarketCap())))
                            .toList();
                    return new HeatmapResponse(sector, heatmapStocks);
                })
                .toList();
    }
}
