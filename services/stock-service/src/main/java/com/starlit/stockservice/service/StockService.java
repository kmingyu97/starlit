package com.starlit.stockservice.service;

import com.starlit.stockservice.common.exception.CustomException;
import com.starlit.stockservice.common.exception.ErrorCode;
import com.starlit.stockservice.dto.PopularStockResponse;
import com.starlit.stockservice.dto.StockDetailResponse;
import com.starlit.stockservice.dto.StockResponse;
import com.starlit.stockservice.entity.SearchLog;
import com.starlit.stockservice.entity.StockDailyPrice;
import com.starlit.stockservice.entity.StockMaster;
import com.starlit.stockservice.repository.SearchLogRepository;
import com.starlit.stockservice.repository.StockDailyPriceRepository;
import com.starlit.stockservice.repository.StockMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 종목 조회 서비스.
 *
 * <p>종목 검색, 상세 조회, 인기 검색 집계 등의 비즈니스 로직을 담당한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockMasterRepository stockMasterRepository;
    private final StockDailyPriceRepository priceRepository;
    private final SearchLogRepository searchLogRepository;

    public StockService(StockMasterRepository stockMasterRepository,
                        StockDailyPriceRepository priceRepository,
                        SearchLogRepository searchLogRepository) {
        this.stockMasterRepository = stockMasterRepository;
        this.priceRepository = priceRepository;
        this.searchLogRepository = searchLogRepository;
    }

    /**
     * 종목 목록을 조회한다. 키워드가 있으면 검색, 없으면 전체 목록을 반환한다.
     *
     * @param keyword  검색 키워드 (종목명 또는 코드, nullable)
     * @param market   시장 필터 (KOSPI/KOSDAQ, nullable)
     * @param pageable 페이징 정보
     * @return 종목 목록 (최근 시세 포함)
     */
    public Page<StockResponse> getStocks(String keyword, String market, Pageable pageable) {
        Page<StockMaster> stocks;

        if (keyword != null && !keyword.isBlank()) {
            stocks = stockMasterRepository.searchByKeyword(keyword.trim(), pageable);
        } else if (market != null && !market.isBlank()) {
            stocks = stockMasterRepository.findByMarket(market.trim(), pageable);
        } else {
            stocks = stockMasterRepository.findAll(pageable);
        }

        return stocks.map(this::toStockResponse);
    }

    /**
     * 종목 상세 정보를 조회한다. 검색 로그를 기록한다.
     *
     * @param stockCode 종목 코드
     * @return 종목 상세 (기본 정보 + 최근 시세)
     * @throws CustomException 종목이 존재하지 않으면 STOCK_NOT_FOUND
     */
    @Transactional
    public StockDetailResponse getStockDetail(String stockCode) {
        StockMaster stock = stockMasterRepository.findById(stockCode)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

        // 검색 로그 기록
        searchLogRepository.save(new SearchLog(stockCode));

        return priceRepository.findFirstByStockCodeOrderByTradeDateDesc(stockCode)
                .map(price -> new StockDetailResponse(
                        stock.getStockCode(), stock.getStockName(), stock.getMarket(),
                        stock.getSector(), stock.getMarketCap(),
                        price.getTradeDate(), price.getOpenPrice(), price.getHighPrice(),
                        price.getLowPrice(), price.getClosePrice(), price.getVolume(),
                        price.getChangeRate()))
                .orElse(new StockDetailResponse(
                        stock.getStockCode(), stock.getStockName(), stock.getMarket(),
                        stock.getSector(), stock.getMarketCap(),
                        null, null, null, null, null, null, null));
    }

    /**
     * 인기 검색 종목 TOP 10을 조회한다.
     *
     * @return 검색 횟수 기준 내림차순 종목 목록 (최대 10개)
     */
    public List<PopularStockResponse> getPopularStocks() {
        List<Object[]> popular = searchLogRepository.findPopularStockCodes();
        List<PopularStockResponse> result = new java.util.ArrayList<>();

        int rank = 1;
        for (Object[] row : popular) {
            if (rank > 10) break;
            String code = (String) row[0];
            long count = (Long) row[1];
            String name = stockMasterRepository.findById(code)
                    .map(StockMaster::getStockName)
                    .orElse(code);
            result.add(new PopularStockResponse(rank++, code, name, count));
        }

        return result;
    }

    /** StockMaster + 최근 시세를 StockResponse로 변환한다. */
    private StockResponse toStockResponse(StockMaster stock) {
        return priceRepository.findFirstByStockCodeOrderByTradeDateDesc(stock.getStockCode())
                .map(price -> new StockResponse(
                        stock.getStockCode(), stock.getStockName(), stock.getMarket(),
                        stock.getSector(), price.getClosePrice(), price.getChangeRate(),
                        stock.getMarketCap()))
                .orElse(new StockResponse(
                        stock.getStockCode(), stock.getStockName(), stock.getMarket(),
                        stock.getSector(), null, null, stock.getMarketCap()));
    }
}
