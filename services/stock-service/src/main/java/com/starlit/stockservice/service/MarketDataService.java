package com.starlit.stockservice.service;

import com.starlit.stockservice.dto.ExchangeRateResponse;
import com.starlit.stockservice.dto.IndexResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 시장 데이터 서비스 (지수·환율).
 *
 * <p>현재는 샘플 데이터를 반환하며, 추후 한국투자증권 API 연동 시 실시간 데이터로 교체된다.</p>
 */
@Service
public class MarketDataService {

    /**
     * 주요 지수를 조회한다 (코스피, 코스닥, 다우, 나스닥).
     *
     * @return 주요 지수 목록
     */
    public List<IndexResponse> getIndices() {
        return List.of(
                new IndexResponse("코스피",
                        new BigDecimal("2650.25"), new BigDecimal("15.30"), new BigDecimal("0.58")),
                new IndexResponse("코스닥",
                        new BigDecimal("875.42"), new BigDecimal("-3.21"), new BigDecimal("-0.37")),
                new IndexResponse("다우존스",
                        new BigDecimal("42350.80"), new BigDecimal("125.50"), new BigDecimal("0.30")),
                new IndexResponse("나스닥",
                        new BigDecimal("18200.15"), new BigDecimal("-45.20"), new BigDecimal("-0.25"))
        );
    }

    /**
     * 주요 환율을 조회한다 (USD, JPY, EUR).
     *
     * @return 환율 목록
     */
    public List<ExchangeRateResponse> getExchangeRates() {
        return List.of(
                new ExchangeRateResponse("USD",
                        new BigDecimal("1380.50"), new BigDecimal("-2.30"), new BigDecimal("-0.17")),
                new ExchangeRateResponse("JPY",
                        new BigDecimal("920.15"), new BigDecimal("1.50"), new BigDecimal("0.16")),
                new ExchangeRateResponse("EUR",
                        new BigDecimal("1505.20"), new BigDecimal("-5.10"), new BigDecimal("-0.34"))
        );
    }
}
